package com.bamboo.assur.partnerinsurers.registry.infrastructure.config

import com.bamboo.assur.partnerinsurers.registry.domain.enums.PartnerInsurerStatus
import io.r2dbc.postgresql.codec.Json
import io.r2dbc.spi.ConnectionFactory
import kotlinx.serialization.json.JsonElement
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions

@Configuration
class R2dbcConfig(
    private val connectionFactory: ConnectionFactory
) : AbstractR2dbcConfiguration() {

    override fun connectionFactory(): ConnectionFactory = connectionFactory

    @Bean
    override fun r2dbcCustomConversions(): R2dbcCustomConversions {
        val converters: List<Any> = listOf(
            // Json Element <-> Postgres Json codec converters
            JsonElementToPostgresJsonConverter(),
            PostgresJsonToJsonElementConverter(),
            // Partner Insurer Status - custom converters for PostgreSQL enum compatibility
            PartnerInsurerStatusWritingConverter(),
            PartnerInsurerStatusReadingConverter()
        )
        // R2dbcCustomConversions constructor expects store conversions + converters
        return R2dbcCustomConversions(storeConversions, converters)
    }

    /*
        Json Element <-> Postgres Json Converters
    */
    @WritingConverter
    class JsonElementToPostgresJsonConverter : Converter<JsonElement, Json> {
        override fun convert(source: JsonElement): Json = Json.of(source.toString())
    }

    @ReadingConverter
    class PostgresJsonToJsonElementConverter : Converter<Json, JsonElement> {
        private val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
        private val logger = LoggerFactory.getLogger(javaClass)

        override fun convert(source: Json): JsonElement {
            logger.debug("Converting PostgresJson to JsonElement: {}", source)
            logger.debug("Converting PostgresJson to JsonElement: Json{}", json.parseToJsonElement(source.asString()))
            return json.parseToJsonElement(source.asString())
        }
    }

    /*
        Partner Insurer Status Converters - custom converters for PostgreSQL enum compatibility
    */
    @WritingConverter
    class PartnerInsurerStatusWritingConverter : Converter<PartnerInsurerStatus, String> {
        override fun convert(source: PartnerInsurerStatus): String = source.name.uppercase()
    }

    @ReadingConverter
    class PartnerInsurerStatusReadingConverter : Converter<String, PartnerInsurerStatus> {
        override fun convert(source: String): PartnerInsurerStatus = PartnerInsurerStatus.valueOf(source.uppercase())
    }
}