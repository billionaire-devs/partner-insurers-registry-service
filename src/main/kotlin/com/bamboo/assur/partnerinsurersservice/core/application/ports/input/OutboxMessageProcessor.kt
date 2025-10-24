package com.bamboo.assur.partnerinsurersservice.core.application.ports.input

import com.bamboo.assur.partnerinsurersservice.core.application.ports.output.OutboxRepository
import com.bamboo.assur.partnerinsurersservice.core.infrastructure.outbox.config.OutboxProperties
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import java.time.Instant
import kotlin.uuid.ExperimentalUuidApi

/**
 * Periodically processes unprocessed outbox messages and publishes them to RabbitMQ.
 */
@Suppress("TooGenericExceptionCaught")
@OptIn(ExperimentalUuidApi::class, ExperimentalAtomicApi::class)
@Component
class OutboxMessageProcessor(
    private val outboxRepository: OutboxRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val outboxProperties: OutboxProperties,
    private val transactionalOperator: TransactionalOperator,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val isProcessing = AtomicBoolean(false)

    /**
     * Scheduled task that polls and processes outbox messages.
     * Only one runner executes at a time per instance.
     * Each message is processed in its own transaction.
     */
    @Scheduled(fixedDelayString = "#{@outboxProcessingInterval}")
    fun processOutbox() {
        if (!isProcessing.compareAndSet(expectedValue = false, newValue = true)) {
            logger.debug("Outbox processing already in progress, skipping this run")
            return
        }

        try {
            runBlocking {
                // Fetch messages outside transaction to avoid long-running transaction
                val messages = outboxRepository.findUnprocessedMessages(outboxProperties.batchSize).toList()

                logger.debug("Found {} unprocessed messages (batchSize={})", messages.size, outboxProperties.batchSize)

                messages.forEach { message ->
                    try {
                        // Build a short preview of the payload to include in logs (safe for debugging)
                        val payloadPreview = try {
                            val s = message.payload.toString()
                            if (s.length > 512) s.substring(0, 512) + "..." else s
                        } catch (_: Exception) {
                            "<unserializable-payload>"
                        }

                        // Convert payload to JSON string for Rabbit (SimpleMessageConverter expects String/byte[]/Serializable)
                        val payloadString = try {
                            message.payload.toString()
                        } catch (_: Exception) {
                            logger.warn("Unable to serialize outbox payload to string for outboxId={}", message.id)
                            "{}"
                        }

                        // Process each message in its own transaction
                        transactionalOperator.executeAndAwait {
                            val routingKey = "${message.aggregateType}.${message.eventType}"

                            logger.debug(
                                "Publishing outbox message: outboxId={}, aggregateId={}, routingKey={}, payloadPreview={}",
                                message.id, message.aggregateId, routingKey, payloadPreview
                            )

                            // Publish the JSON payload string
                            rabbitTemplate.convertAndSend(
                                "partner-insurers.direct",
                                routingKey,
                                payloadString
                            )

                            logger.debug("Message published to exchange (outboxId={}, routingKey={}, payloadSize={})", message.id, routingKey, payloadString.length)

                            // Mark as processed in same transaction and check update count
                            val updated = outboxRepository.markAsProcessed(
                                id = message.id,
                                processedAt = Instant.now(),
                                error = null
                            )

                            if (updated == 1) {
                                logger.debug("Marked outbox message as processed: {}", message.id)
                            } else {
                                logger.warn(
                                    "markAsProcessed returned {} for outboxId={} — row may not exist or was concurrently modified",
                                    updated, message.id
                                )
                            }
                        }

                        logger.debug("Processed outbox message: {}", message.id)
                    } catch (e: Exception) {
                        logger.error("Failed to process outbox message: {}", message.id, e)
                        // Mark as failed in separate transaction
                        try {
                            val errMsg = (e.message ?: e.toString()).let {
                                if (it.length > 1024) it.substring(0, 1024) + "..." else it
                            }

                            transactionalOperator.executeAndAwait {
                                val updated = outboxRepository.markAsProcessed(
                                    id = message.id,
                                    processedAt = Instant.now(),
                                    error = errMsg
                                )
                                if (updated == 1) {
                                    logger.debug("Marked failed outbox message as processed (error stored): {}", message.id)
                                } else {
                                    logger.warn("Failed to mark message as failed, markAsProcessed returned {} for {}", updated, message.id)
                                }
                            }
                        } catch (markError: Exception) {
                            logger.error("Failed to mark message as failed: {}", message.id, markError)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Error in outbox processing job", e)
        } finally {
            isProcessing.store(false)
        }
    }
}
