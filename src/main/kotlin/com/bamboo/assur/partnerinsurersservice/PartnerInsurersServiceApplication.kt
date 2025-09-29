package com.bamboo.assur.partnerinsurersservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class PartnerInsurersServiceApplication

fun main(args: Array<String>) {
    runApplication<PartnerInsurersServiceApplication>(*args)
}

@RestController
class HealthController {

    @GetMapping("/")
    fun index(): String {
        return "Partner Insurers Service"
    }

    @GetMapping("/health")
    fun health(): String {
        return "OK"
    }
}
