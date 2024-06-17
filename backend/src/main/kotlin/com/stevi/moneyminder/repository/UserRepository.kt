package com.stevi.moneyminder.repository;

import com.stevi.moneyminder.entity.User
import java.util.*
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, UUID> {

    fun findByEmail(username: String): User?
}