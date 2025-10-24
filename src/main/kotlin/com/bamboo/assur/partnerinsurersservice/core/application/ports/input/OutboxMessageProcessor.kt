package com.bamboo.assur.partnerinsurersservice.core.application.ports.input

import com.bamboo.assur.partnerinsurersservice.core.application.ports.output.OutboxRepository
import com.bamboo.assur.partnerinsurersservice.core.infrastructure.outbox.config.OutboxProperties
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import java.time.Instant
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
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
                // Process messages up to batchSize, each in its own short transaction.
                logger.debug("Attempting to process up to {} messages (per-message transactional with FOR UPDATE SKIP LOCKED)", outboxProperties.batchSize)

                var processedCount = 0
                repeat(outboxProperties.batchSize) {
                    // Each iteration: open a small transaction, fetch one row FOR UPDATE SKIP LOCKED, publish, mark processed.
                    try {
                        val processedId = transactionalOperator.executeAndAwait {
                            // fetch and lock a single row
                            val message = outboxRepository.fetchNextUnprocessedForUpdateSkipLocked()
                            if (message == null) return@executeAndAwait null

                            // Build a short preview and the payload string
                            val payloadPreview = try {
                                val s = message.payload.toString()
                                if (s.length > 512) s.substring(0, 512) + "..." else s
                            } catch (_: Exception) {
                                "<unserializable-payload>"
                            }

                            val payloadString = try {
                                message.payload.toString()
                            } catch (_: Exception) {
                                logger.warn("Unable to serialize outbox payload to string for outboxId={}", message.id)
                                "{}"
                            }

                            val routingKey = "${message.aggregateType}.${message.eventType}"
                            logger.debug(
                                "Publishing outbox message (locked): outboxId={}, aggregateId={}, routingKey={}, payloadPreview={}",
                                message.id, message.aggregateId, routingKey, payloadPreview
                            )

                            // Publish while holding the DB lock (short-lived). This guarantees no concurrent processor picks same row.
                            rabbitTemplate.convertAndSend("partner-insurers.direct", routingKey, payloadString)

                            logger.debug("Published outbox message (locked): outboxId={}, routingKey={}, payloadSize={}", message.id, routingKey, payloadString.length)

                            // Mark as processed in the same transaction
                            val updated = outboxRepository.markAsProcessed(id = message.id, processedAt = Instant.now(), error = null)
                            if (updated == 1) {
                                logger.debug("Marked outbox message as processed: {}", message.id)
                            } else {
                                logger.warn("markAsProcessed returned {} for outboxId={} — row may not exist or was concurrently modified", updated, message.id)
                            }

                            // return id
                            message.id
                        }

                        if (processedId == null) {
                            // No more rows to process
                            return@runBlocking
                        }

                        processedCount++
                        logger.debug("Processed outbox message: {} (count={})", processedId, processedCount)
                    } catch (e: Exception) {
                        // If something went wrong inside the transaction, we might not have message id.
                        // Log and continue to next iteration.
                        logger.error("Unexpected error while processing outbox in iteration (will continue): {}", e.message, e)
                    }
                }
                logger.debug("Outbox processing run finished, total processed={}", processedCount)
            }
        } catch (e: Exception) {
            logger.error("Error in outbox processing job", e)
        } finally {
            isProcessing.store(false)
        }
    }
}
