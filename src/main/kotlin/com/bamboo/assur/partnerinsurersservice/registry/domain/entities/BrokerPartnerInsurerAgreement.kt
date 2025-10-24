@file:OptIn(ExperimentalUuidApi::class)

package com.bamboo.assur.partnerinsurersservice.registry.domain.entities

import com.bamboo.assur.partnerinsurersservice.core.domain.Model
import com.bamboo.assur.partnerinsurersservice.core.domain.insurance.InsuranceBranch
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.DomainEntityId
import com.bamboo.assur.partnerinsurersservice.core.domain.valueObjects.Url
import com.bamboo.assur.partnerinsurersservice.registry.domain.enums.AgreementStatus
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateRange
import kotlinx.datetime.until
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Suppress("LongParameterList")
@OptIn(ExperimentalTime::class)
class BrokerPartnerInsurerAgreement private constructor(
    id: DomainEntityId,
    val partnerId: DomainEntityId,
    var agreementCode: String?,
    var agreementTitle: String,
    var startDate: LocalDate,
    var endDate: LocalDate?,
    var status: AgreementStatus,
    var coveredBranches: Set<InsuranceBranch>,
    var representatives: Set<Contact>,
    var paymentTerms: PaymentTerms,
    var documentRefUrl: Url?,
    var signedAt: Instant?,
    createdAt: Instant = Clock.System.now(),
    updatedAt: Instant = createdAt
): Model(id, createdAt, updatedAt) {

    companion object {
        fun create(
            partnerId: UUID,
            agreementCode: String?,
            agreementTitle: String,
            branches: Set<InsuranceBranch>,
            startDate: LocalDate,
            endDate: LocalDate?,
            documentRefUrl: Url?,
            representatives: Set<Contact>,
            paymentTerms: PaymentTerms,
        ): BrokerPartnerInsurerAgreement {
            require(branches.isNotEmpty()) { "Le contrat doit couvrir au moins une branche." }
            return BrokerPartnerInsurerAgreement(
                id = DomainEntityId.random(),
                partnerId = DomainEntityId(partnerId),
                agreementCode = agreementCode,
                agreementTitle = agreementTitle,
                startDate = startDate,
                endDate = endDate,
                status = AgreementStatus.DRAFT,
                coveredBranches = branches,
                documentRefUrl = documentRefUrl,
                signedAt = null,
                representatives = representatives,
                paymentTerms = paymentTerms,
            )
        }
    }

    val validityPeriod: LocalDateRange? = endDate?.let {
        LocalDateRange(startDate, it)
    }

    fun validate() {
        require(status == AgreementStatus.DRAFT) { "Seuls les contrats brouillons peuvent être activés." }
        require(status !in listOf(AgreementStatus.ACTIVE, AgreementStatus.PENDING)) { "Le contrat a déja été validé" }

        status = if (
            Clock.System.now().until(
                Instant.fromEpochMilliseconds(startDate.toEpochDays()),
                DateTimeUnit.MILLISECOND
            ) > 0
        ) {
            AgreementStatus.PENDING
        } else {
            AgreementStatus.ACTIVE
        }
        touch()
    }

    fun suspend(reason: String?) {
        require(status == AgreementStatus.ACTIVE) { "Seuls les contrats actifs peuvent être suspendus." }
        require(status != AgreementStatus.SUSPENDED) { "Le contrat est déjà suspendu." }

        status = AgreementStatus.SUSPENDED
        touch()
    }

    fun terminate(reason: String?) {
        require(status != AgreementStatus.SUSPENDED) { "Seuls les contrats actifs peuvent être résiliés." }
        require(status != AgreementStatus.TERMINATED) { "Le contrat est déjà résilié." }

        status = AgreementStatus.TERMINATED
        touch()
    }
}