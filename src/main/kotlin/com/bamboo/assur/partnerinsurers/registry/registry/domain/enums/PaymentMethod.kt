package com.bamboo.assur.partnerinsurers.registry.registry.domain.enums

/**
 * Represents the payment method configured in a partner contract.
 *
 * This model is hierarchical:
 * - PaymentMethod encapsulates the payment method.
 * - Each method encapsulates its providers.
 * - Each provider encapsulates its specific data (account, phone, etc.).
 */
sealed class PaymentMethod(
    open val currency: String = "XAF",
    open val isAutomatic: Boolean = true,
    open val requiresApproval: Boolean = false
) {

    /**
     * Payment via traditional bank (interbank transfer, BEAC, etc.)
     *
     * @property provider The bank provider.
     * @property account The bank account details.
     * @property isAutomatic Indicates if the payment is processed automatically. Defaults to `false`.
     * @property requiresApproval Indicates if the payment requires manual approval. Defaults to `true`.
     */
    data class BankTransfer(
        val provider: BankProvider,
        val account: BankAccount,
        override val isAutomatic: Boolean = false,
        override val requiresApproval: Boolean = true
    ) : PaymentMethod()

    /**
     * Payment via mobile operator (Airtel Money, MoMo, etc.)
     *
     * @property provider The mobile money provider.
     * @property phoneNumber The phone number associated with the mobile money account. Can be `null` if `accountCode` is provided.
     * @property accountCode The account code associated with the mobile money account. Can be `null` if `phoneNumber` is provided.
     * @property isAutomatic Indicates if the payment is processed automatically. Defaults to `true`.
     * @throws IllegalArgumentException if both `phoneNumber` and `accountCode` are `null`.
     */
    data class MobileMoney(
        val provider: MobileMoneyProvider,
        val phoneNumber: String? = null,
        val accountCode: String? = null,
        override val isAutomatic: Boolean = true
    ) : PaymentMethod() {
        init {
            require(phoneNumber != null || accountCode != null) {
                "Either phoneNumber or accountCode must be provided for MobileMoney."
            }
        }
    }

    /**
     * Manual payment: cash or check.
     *
     * @property mode The manual payment mode (CHEQUE or ESPECES).
     * @property reference An optional reference for the manual payment.
     * @property isAutomatic Indicates if the payment is processed automatically. Defaults to `false`.
     * @property requiresApproval Indicates if the payment requires manual approval. Defaults to `true`.
     */
    data class Manual(
        val mode: ManualMode,
        val reference: String? = null,
        override val isAutomatic: Boolean = false,
        override val requiresApproval: Boolean = true
    ) : PaymentMethod()
}

/**
 * List of main banks in the CEMAC zone. This list is not exhaustive.
 */
enum class BankProvider {
    /** BGFIBank */
    BGFI, UBA, ECOBANK, ORABANK, BICIG, POSTEBANK, LOA, UGB, CITIBANK, ALIOS, FINATRA, AUTRE
}

/**
 * Details of a local bank account (BEAC). This can be a RIB or an international account.
 *
 * @property rib The Relevé d'Identité Bancaire (RIB) or bank account number.
 * @property accountHolder The name of the account holder.
 * @property swiftCode The SWIFT/BIC code for international transfers. Can be `null`.
 */
data class BankAccount(
    val rib: String,
    val accountHolder: String,
    val swiftCode: String? = null
)

/**
 * Mobile Money providers. This list is not exhaustive.
 */
enum class MobileMoneyProvider {
    /** Airtel Money */
    AIRTEL_MONEY,

    /** Moov Money */
    MOOV_MONEY,

    /** Wave Mobile Money */
    WAVE,

    /** Express Union Mobile Money */
    EXPRESS_UNION,

    /** Click Pay */
    CLICK_PAY,

    /** Other mobile money provider */
    AUTRE
}

/**
 * Manual payment modes.
 */
enum class ManualMode {
    /** Payment by check */
    CHEQUE, ESPECES
}