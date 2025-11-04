package com.bamboo.assur.partnerinsurers.registry.core.application.ports.input

import com.bamboo.assur.partnerinsurers.registry.core.application.ports.output.OutboxRepository
import com.bamboo.assur.partnerinsurers.registry.core.infrastructure.outbox.config.OutboxProperties
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
    @Scheduled(fixedDelayString = "\${partner-insurers.outbox.processing-interval-millis:30000}")
    fun processOutbox() {
        if (!isProcessing.compareAndSet(expectedValue = false, newValue = true)) {
            logger.debug("Outbox processing already in progress")
            return
        }

        try {
            runBlocking {
                var processedCount = 0
                while (processedCount < outboxProperties.batchSize) {
                    if (!processNextMessage()) break
                    processedCount++
                }
                if (processedCount > 0) {
                    logger.debug("Processed {} outbox messages", processedCount)
                }
            }
        } finally {
            isProcessing.store(false)
        }
    }

    private suspend fun processNextMessage(): Boolean {
        return try {
            transactionalOperator.executeAndAwait {
                val message = outboxRepository.fetchNextUnprocessedForUpdateSkipLocked() ?: return@executeAndAwait false

                val routingKey = "${message.aggregateType}.${message.eventType}"
                rabbitTemplate.convertAndSend(
                    "partner-insurers.registry.direct",
                    routingKey,
                    message.payload.toString()
                )
                outboxRepository.markAsProcessed(message.id, Instant.now())
                true
            }
        } catch (e: Exception) {
            logger.error("Failed to process outbox message: {}", e.message, e)
            false
        }
    }
}
