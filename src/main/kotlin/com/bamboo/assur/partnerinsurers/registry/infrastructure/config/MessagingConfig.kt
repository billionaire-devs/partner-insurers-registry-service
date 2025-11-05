package com.bamboo.assur.partnerinsurers.registry.infrastructure.config

import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Global RabbitMQ configuration for the application.
 * Provides common messaging infrastructure beans.
 */
@Configuration
class MessagingConfig {
    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        return RabbitTemplate(connectionFactory).apply {
            messageConverter = JacksonJsonMessageConverter()
        }
    }

    @Bean
    fun mainExchange(): DirectExchange {
        return DirectExchange("partner-insurers.registry.direct", true, false)
    }
}