package com.stevi.moneyminder.repository;

import com.stevi.moneyminder.entity.Account
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<Account, UUID> {
}