package com.bamboo.assur.partnerinsurersservice

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.PostgreSQLR2DBCDatabaseContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class PartnerInsurersServiceApplicationTests {

    companion object {
        @Container
        private val postgres = PostgreSQLContainer("postgres:alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            PostgreSQLR2DBCDatabaseContainer.getOptions(postgres)

            registry.add("spring.r2dbc.url") {
                "r2dbc:postgresql://${postgres.host}:${
                    postgres.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)
                }/${postgres.databaseName}"
            }
            registry.add("spring.r2dbc.username", postgres::getUsername)
            registry.add("spring.r2dbc.password", postgres::getPassword)

            // Also set JDBC URL for Flyway
            registry.add("spring.flyway.url", postgres::getJdbcUrl)
            registry.add("spring.flyway.user", postgres::getUsername)
            registry.add("spring.flyway.password", postgres::getPassword)
        }
    }

    @Test
    fun contextLoads() {
        // This test will now start a PostgreSQL container and verify the application context loads
    }
}
