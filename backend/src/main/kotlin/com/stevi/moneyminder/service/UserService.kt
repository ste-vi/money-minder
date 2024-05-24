package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.Space
import com.stevi.moneyminder.entity.User
import com.stevi.moneyminder.repository.SpaceRepository
import com.stevi.moneyminder.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository, private val spaceRepository: SpaceRepository) {

    fun login() {
        val user = userRepository.save(User(null, "user"))
        spaceRepository.save(Space(null, "Default", user))
    }

}
