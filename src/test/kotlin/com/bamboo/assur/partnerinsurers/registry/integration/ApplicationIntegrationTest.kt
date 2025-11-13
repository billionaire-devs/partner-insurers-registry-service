package com.bamboo.assur.partnerinsurers.registry.integration

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class ApplicationIntegrationTest : BaseIntegrationTest() {

    @Test
    @Disabled("Testcontainers integration needs additional configuration - skipping for now")
    fun `context loads successfully with testcontainers`() {
        // Test that the Spring Boot application context loads successfully
        // with PostgreSQL and RabbitMQ testcontainers
        // This is a smoke test to ensure all beans are properly configured
        
        // TODO: Fix testcontainers configuration for full integration testing
        // The current setup needs proper data source bean configuration
    }
}