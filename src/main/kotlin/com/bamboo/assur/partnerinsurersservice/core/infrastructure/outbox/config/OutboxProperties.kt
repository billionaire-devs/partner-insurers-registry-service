package com.bamboo.assur.partnerinsurersservice.core.infrastructure.outbox.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Configuration properties for the outbox pattern.
 */
@ConfigurationProperties(prefix = "partner-insurers.outbox")
data class OutboxProperties(
    /**
     * Interval between outbox processing runs in milliseconds
     */
    val processingIntervalMillis: Long = 30000L,
    
    /**
     * Number of messages to process in one batch
     */
    val batchSize: Int = 100,
    
    /**
     * Whether to enable the outbox processor
     */
    val enabled: Boolean = true
) {
    /**
     * Processing interval as Duration for convenience
     */
    val processingInterval: Duration
        get() = processingIntervalMillis.milliseconds
}
