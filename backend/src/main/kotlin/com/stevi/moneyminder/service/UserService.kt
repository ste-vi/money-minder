package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.User
import com.stevi.moneyminder.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository) {

    fun login() {
        userRepository.save(User(null, "user"))
    }


}
