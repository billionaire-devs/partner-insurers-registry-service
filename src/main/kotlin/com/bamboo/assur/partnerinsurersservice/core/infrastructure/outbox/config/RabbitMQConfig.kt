package com.bamboo.assur.partnerinsurersservice.core.infrastructure.outbox.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * RabbitMQ configuration for publishing outbox events.
 *
 * We send messages as plain JSON strings via the default converter, avoiding a hard
 * dependency on Jackson since serialization is handled by Kotlinx at the edges.
 */
@Configuration
class RabbitMQConfig {

    @Bean
    fun rabbitTemplate(
        connectionFactory: ConnectionFactory
    ): RabbitTemplate {
        return RabbitTemplate(connectionFactory)
    }

    @Bean
    fun outboxExchange(): DirectExchange {
        return DirectExchange("partner-insurers.direct", true, false)
    }

    @Bean
    fun outboxQueue(): Queue {
        return Queue("partner-insurers.events", true)
    }

    @Bean
    fun binding(outboxQueue: Queue, outboxExchange: DirectExchange): Binding {
        return BindingBuilder.bind(outboxQueue)
            .to(outboxExchange)
            .with("partner-insurers.event")
    }
}
