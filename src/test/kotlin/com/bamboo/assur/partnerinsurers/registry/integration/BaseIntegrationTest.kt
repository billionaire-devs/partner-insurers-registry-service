package com.bamboo.assur.partnerinsurers.registry.integration

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestContainersConfiguration::class)
@Testcontainers
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
abstract class BaseIntegrationTest