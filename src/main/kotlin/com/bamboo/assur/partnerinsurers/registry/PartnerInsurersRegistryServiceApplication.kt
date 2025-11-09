package com.bamboo.assur.partnerinsurers.registry

import com.bamboo.assur.partnerinsurers.sharedkernel.presentation.ApiResponseBodyAdvice
import com.bamboo.assur.partnerinsurers.sharedkernel.presentation.GlobalExceptionHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(ApiResponseBodyAdvice::class, GlobalExceptionHandler::class)
class PartnerInsurersRegistryServiceApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<PartnerInsurersRegistryServiceApplication>(*args)
}
