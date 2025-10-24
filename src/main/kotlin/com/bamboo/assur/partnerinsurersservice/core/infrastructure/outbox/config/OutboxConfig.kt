package com.bamboo.assur.partnerinsurersservice.core.infrastructure.outbox.config

import com.bamboo.assur.partnerinsurersservice.core.application.ports.input.OutboxMessageProcessor
import com.bamboo.assur.partnerinsurersservice.core.application.ports.output.OutboxRepository
import com.bamboo.assur.partnerinsurersservice.core.infrastructure.serialization.DomainEntityIdSerializer
import com.bamboo.assur.partnerinsurersservice.core.infrastructure.serialization.InstantSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.reactive.TransactionalOperator
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

/**
 * Configuration for Transactional Outbox processing.
 *
 * - Provides a Kotlinx [Json] bean with custom serializers.
 * - Wires the scheduled [OutboxMessageProcessor].
 */
@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@Configuration
@EnableScheduling
@EnableConfigurationProperties(OutboxProperties::class)
class OutboxConfig {

    /**
     * Shared Kotlinx JSON instance used for serializing events.
     * Includes custom serializers for domain value objects.
     */
    @Bean
    fun kotlinxJson(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
        serializersModule = SerializersModule {
            contextual(DomainEntityIdSerializer)
            contextual(InstantSerializer)
        }
    }

    @Bean
    @ConditionalOnProperty(
        prefix = "partner-insurers.outbox",
        name = ["enabled"],
        havingValue = "true",
        matchIfMissing = true
    )
    fun outboxMessageProcessor(
        outboxRepository: OutboxRepository,
        rabbitTemplate: RabbitTemplate,
        transactionalOperator: TransactionalOperator,
        outboxProperties: OutboxProperties
    ): OutboxMessageProcessor {
        return OutboxMessageProcessor(
            outboxRepository = outboxRepository,
            rabbitTemplate = rabbitTemplate,
            outboxProperties = outboxProperties,
            transactionalOperator = transactionalOperator,
        )
    }

    @Bean
    fun outboxProcessingInterval(outboxProperties: OutboxProperties): Long {
        return outboxProperties.processingIntervalMillis
    }
}
