package com.bamboo.assur.partnerinsurersservice.core.infrastructure.serialization

import com.bamboo.assur.partnerinsurersservice.core.infrastructure.serialization.DomainEntityIdSerializer
import com.bamboo.assur.partnerinsurersservice.core.infrastructure.serialization.InstantSerializer
import com.bamboo.assur.partnerinsurersservice.core.infrastructure.serialization.UrlSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@Configuration
class SerializationConfig {
    /**
     * Global JSON configuration for the application.
     * Used by all features that need JSON serialization.
     */
    @Bean
    fun kotlinxJson(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
        serializersModule = SerializersModule {
            contextual(DomainEntityIdSerializer)
            contextual(UrlSerializer)
            contextual(InstantSerializer)
        }
    }
}
