package com.stevi.moneyminder.service

import com.stevi.moneyminder.entity.Currency
import com.stevi.moneyminder.entity.Space
import com.stevi.moneyminder.entity.User
import com.stevi.moneyminder.model.response.UserResponse
import com.stevi.moneyminder.repository.SpaceRepository
import com.stevi.moneyminder.repository.UserRepository
import java.time.LocalDateTime
import java.util.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val spaceRepository: SpaceRepository,
    private val categoryService: CategoryService
) {

    @Transactional(readOnly = true)
    fun getCurrentUserResponse(userId: UUID): UserResponse {
        val user = userRepository.findById(userId).orElseThrow()
        return user.let {
            UserResponse(
                email = it.email,
                username = it.username
            )
        }
    }

    @Transactional(readOnly = true)
    fun findByEmail(username: String): User? {
        return userRepository.findByEmail(username)
    }

    @Transactional(readOnly = true)
    fun createNewUser(email: String, username: String): User {
        val user = userRepository.save(User(null, email, username, null))
        val space = initSpaceForUser(user)
        user.lastLoggedInSpaceId = space.id
        userRepository.save(user)
        return user
    }

    private fun initSpaceForUser(user: User): Space {
        val space = spaceRepository.save(Space(null, "Personal", user, Currency.UAH, LocalDateTime.now(), LocalDateTime.now()))
        categoryService.initDefaultCategories(space)
        return space
    }
}
