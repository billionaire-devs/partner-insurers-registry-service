package com.bamboo.assur.partnerinsurersservice

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PartnerInsurersServiceApplicationTests {

    @Disabled("Disabled in unit test runs to avoid integration DB dependency")
    @Test
    fun contextLoads() {
        // Integration-style context loading test; disabled by default in unit test runs.
        assert(true)
    }
}
