package com.bamboo.assur.partnerinsurers.registry.infrastructure.config

import com.bamboo.assur.partnerinsurers.registry.infrastructure.outbox.OutboxMessageProcessor
import com.bamboo.assur.partnerinsurers.registry.infrastructure.outbox.OutboxProperties
import com.bamboo.assur.partnerinsurers.registry.infrastructure.persistence.repositories.OutboxRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.reactive.TransactionalOperator

/**
 * Configuration for Transactional Outbox pattern.
 * Configures the message processor for reliable event delivery.
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties(OutboxProperties::class)
class OutboxConfig {
    @Bean
    @ConditionalOnProperty(
        prefix = "partner-insurers-registry-service.outbox",
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
}