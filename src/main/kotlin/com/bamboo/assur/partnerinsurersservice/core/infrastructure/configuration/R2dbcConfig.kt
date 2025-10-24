package com.bamboo.assur.partnerinsurersservice.core.infrastructure.configuration

import com.bamboo.assur.partnerinsurersservice.registry.domain.enums.PartnerInsurerStatus
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.postgresql.codec.Json as PostgresJson
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
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
    class JsonElementToPostgresJsonConverter : Converter<JsonElement, PostgresJson> {
        override fun convert(source: JsonElement): PostgresJson = PostgresJson.of(source.toString())
    }

    @ReadingConverter
    class PostgresJsonToJsonElementConverter : Converter<PostgresJson, JsonElement> {
        private val json = Json { ignoreUnknownKeys = true }
        override fun convert(source: PostgresJson): JsonElement = json.parseToJsonElement(source.asString())
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
