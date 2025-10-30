package com.bamboo.assur.partnerinsurersservice.registry.domain.entities

import com.bamboo.assur.partnerinsurersservice.registry.domain.enums.InvoicingMethod
import com.bamboo.assur.partnerinsurersservice.registry.domain.enums.PaymentFrequency
import com.bamboo.assur.partnerinsurersservice.registry.domain.enums.PaymentMethod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/**
 * Payment terms between the platform and the partner.
 *
 * Used to automate the generation of due dates and the calculation of penalties.
 *
 * @property commissionRate The percentage of commission to be applied to the premium.
 * Must be greater than 0.
 * @property frequency The frequency of payments (e.g., monthly, quarterly).
 * @property invoicingMethod The method of invoicing.
 * @property paymentDelayDays The number of days after the period end date that payment is due.
 * Must be greater than or equal to 0.
 * @property latePaymentPenaltyRate The penalty rate to be applied for late payments.
 * Must be greater than 0 if defined, otherwise null for no penalty.
 */
data class PaymentTerms(
    val commissionRate: Double,
    val frequency: PaymentFrequency,
    val invoicingMethod: InvoicingMethod,
    val paymentMethod: PaymentMethod,
    val paymentDelayDays: Int,
    val latePaymentPenaltyRate: Double?,
) {
    init {
        require(commissionRate > 0) { "Commission percentage must be greater than 0" }
        require(paymentDelayDays >= 0) { "Payment delay days must be greater than or equal to 0" }
        require(latePaymentPenaltyRate == null || latePaymentPenaltyRate > 0) {
            "Penalty rate must be greater than 0 if defined"
        }
    }

    /**
     * Calculates the net amount to be remitted for a given premium.
     *
     * @param premium The total premium amount.
     * @return The net amount after deducting the commission.
     */
    fun calculateNetAmountToRemit(premium: Double): Double {
        val commission = premium.times(commissionRate).div(100)
        return premium.minus(commission)
    }

    /**
     * Calculates the payment due date based on the billing period start date and payment terms.
     *
     * @param periodStart The start date of the billing period.
     * @return The calculated payment due date.
     */
    fun dueDateFor(periodStart: LocalDate): LocalDate {
        val nextPeriod = when (frequency) {
            PaymentFrequency.DAILY -> periodStart.plus(1, DateTimeUnit.DAY)
            PaymentFrequency.WEEKLY -> periodStart.plus(1, DateTimeUnit.WEEK)
            PaymentFrequency.MONTHLY -> periodStart.plus(1, DateTimeUnit.MONTH)
            PaymentFrequency.QUARTERLY -> periodStart.plus(1, DateTimeUnit.QUARTER)
            PaymentFrequency.SEMI_ANNUALLY -> periodStart.plus(6, DateTimeUnit.MONTH)
            PaymentFrequency.ANNUALLY -> periodStart.plus(1, DateTimeUnit.YEAR)
        }
        return nextPeriod.plus(paymentDelayDays, DateTimeUnit.DAY)
    }

    /**
     * Calculates the commission amount to be remitted for a given premium.
     *
     * @param premium The total premium amount.
     * @return The calculated commission amount.
     */
    fun calculateCommission(premium: Double): Double {
        return premium.times(commissionRate).div(100.0)
    }

    /**
     * Calculates the penalty for a late payment.
     *
     * @param daysLate The number of days the payment is late.
     * @param amountDue The amount that was due.
     * @return The calculated penalty amount. Returns 0.0 if not late or no penalty rate is defined.
     */
    fun penaltyFor(daysLate: Int, amountDue: Double): Double {
        // No penalty if not late or no penalty rate defined
        if (daysLate <= 0 || latePaymentPenaltyRate == null) return 0.0
        return amountDue.times(latePaymentPenaltyRate).times(daysLate)
    }

    /**
     * Checks if a given payment date is late compared to the due date.
     * @param paymentDate The actual date the payment was made.
     * @param dueDate The calculated due date for the payment.
     * @return `true` if the payment date is after the due date, `false` otherwise.
     */
    fun isLate(paymentDate: LocalDate, dueDate: LocalDate): Boolean = paymentDate.minus(dueDate).days > 0

    /**
     * Calculates the number of days a payment is late.
     * @param paymentDate The actual date the payment was made.
     * @param dueDate The calculated due date for the payment.
     * @return The number of days late. Returns 0 if not late.
     */
    fun daysLate(paymentDate: LocalDate, dueDate: LocalDate): Int = maxOf(0, paymentDate.minus(dueDate).days)

    /**
     * Calculates the total amount due, including any penalties for late payment.
     * @param amountDue The original amount that was due.
     * @param paymentDate The actual date the payment was made.
     * @param dueDate The calculated due date for the payment.
     * @return The total amount due, including penalties.
     */
    fun calculateTotalAmountDue(amountDue: Double, paymentDate: LocalDate, dueDate: LocalDate): Double =
        amountDue + penaltyFor(daysLate(paymentDate, dueDate), amountDue)

}
