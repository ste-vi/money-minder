package com.stevi.moneyminder.entity

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
    @Column(name = "currency", nullable = false, updatable = false)
    open var currency: Currency,

    @Column(name = "date", nullable = false)
    open var date: LocalDateTime,

    @Column(name = "mono_bank_id", nullable = true, updatable = false)
    open var monoBankId: String? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(nullable = false)
    open var account: Account,
)