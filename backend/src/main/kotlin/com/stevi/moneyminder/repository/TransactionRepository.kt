package com.stevi.moneyminder.repository;

import com.stevi.moneyminder.entity.Transaction
import java.time.LocalDateTime
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TransactionRepository : JpaRepository<Transaction, UUID> {

    @Query("select t.monoBankId from Transaction t where t.date >= :date")
    fun findMonoBankIdsByDateGreaterThan(date: LocalDateTime): List<String>
}