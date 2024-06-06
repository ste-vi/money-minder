package com.stevi.moneyminder.entity

import com.stevi.moneyminder.model.response.TransactionResponse
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "transactions")
open class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    open var id: UUID? = null,

    @Column(name = "name", nullable = false)
    open var name: String,

    @Column(name = "notes", nullable = true)
    open var notes: String?,

    @Column(name = "amount", nullable = false)
    open var amount: BigDecimal = BigDecimal.ZERO,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "currency", nullable = false, updatable = true)
    open var currency: Currency,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", nullable = false)
    open var type: TransactionType,

    @Column(name = "date", nullable = false)
    open var date: LocalDateTime,

    @Column(name = "created_date", nullable = false, updatable = false)
    open var createdDate: LocalDateTime,

    @Column(name = "mono_bank_id", nullable = true, updatable = false)
    open var monoBankId: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_account_id", nullable = false)
    open var fromAccount: Account,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "to_account_id", nullable = true)
    open var toAccount: Account?,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "category_id")
    open var category: Category? = null
)

fun Transaction.mapToResponse() = TransactionResponse(
    id = this.id ?: UUID.randomUUID(),
    name = this.name,
    notes = this.notes,
    amount = this.amount,
    currency = this.currency.mapToResponse(),
    fromAccount = this.fromAccount.mapToResponse(),
    toAccount = this.toAccount?.mapToResponse(),
    date = this.date,
    category = this.category?.mapToResponse(),
    type = this.type,
    isBankTransaction = this.monoBankId != null
)

fun Transaction.applyRule(rule: Rule): Boolean {
    if (rule.condition.type == ConditionType.TEXT_CONTAINS && this.name.contains(rule.condition.textToApply)) {
        this.category = rule.assignCategory
        return true
    } else if (rule.condition.type == ConditionType.TEXT_EQUALS && this.name == rule.condition.textToApply) {
        this.category = rule.assignCategory
        return true
    }
    return false
}