package com.bamboo.assur.partnerinsurersservice.core.application.ports.input

import com.bamboo.assur.partnerinsurersservice.core.application.ports.output.OutboxRepository
import com.bamboo.assur.partnerinsurersservice.core.infrastructure.outbox.config.OutboxProperties
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toKotlinUuid

/**
 * Periodically processes unprocessed outbox messages and publishes them to RabbitMQ.
 *
 * - Uses Kotlin coroutines/Flow for non-blocking DB access.
 * - Sends plain JSON strings to RabbitMQ (no Jackson dependency).
 */
@Suppress("TooGenericExceptionCaught")
@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class, ExperimentalAtomicApi::class)
@Component
class OutboxMessageProcessor(
    private val outboxRepository: OutboxRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val outboxProperties: OutboxProperties,
    private val transactionalOperator: TransactionalOperator,
    private val json: Json
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
                
                logger.debug("Found {} unprocessed messages", messages.size)
                
                messages.forEach { message ->
                    try {
                        // Process each message in its own transaction
                        transactionalOperator.executeAndAwait {
                            val routingKey = "${message.aggregateType}.${message.eventType}"

                            // Publish the raw JSON payload string
                            rabbitTemplate.convertAndSend(
                                "partner-insurers.direct",
                                routingKey,
                                message.payload
                            )

                            // Mark as processed in same transaction
                            outboxRepository.markAsProcessed(
                                id = message.id.toKotlinUuid(),
                                processedAt = Clock.System.now(),
                                error = null
                            )
                        }

                        logger.debug("Processed outbox message: {}", message.id)
                    } catch (e: Exception) {
                        logger.error("Failed to process outbox message: {}", message.id, e)
                        // Mark as failed in separate transaction
                        try {
                            transactionalOperator.executeAndAwait {
                                outboxRepository.markAsProcessed(
                                    id = message.id.toKotlinUuid(),
                                    processedAt = Clock.System.now(),
                                    error = e.message
                                )
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
