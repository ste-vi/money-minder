package com.stevi.moneyminder.entity

import com.stevi.moneyminder.model.response.AccountResponse
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
@Table(name = "accounts")
open class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    open var id: UUID? = null,

    @Column(name = "name", nullable = false)
    open var name: String,

    @Column(name = "description", nullable = true)
    open var description: String?,

    @Column(name = "balance", nullable = false)
    open var balance: BigDecimal = BigDecimal.ZERO,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "currency", nullable = false, updatable = false)
    open var currency: Currency,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type", nullable = false)
    open var type: AccountType,

    @Column(name = "created_date", nullable = false, updatable = false)
    open var createdDate: LocalDateTime,

    @Column(name = "mono_bank_id", nullable = true, updatable = false)
    open var monoBankId: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    open var space: Space,
)

fun Account.mapToResponse(): AccountResponse {
    return AccountResponse(
        id = id,
        name = name,
        description = description,
        balance = balance,
        currency = currency.mapToResponse(),
        type = type.mapToResponse(),
    )
}