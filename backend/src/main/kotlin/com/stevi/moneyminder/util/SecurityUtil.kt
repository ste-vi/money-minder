package com.stevi.moneyminder.util

import com.stevi.moneyminder.security.CustomUserDetails
import java.util.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

class SecurityUtil {

    companion object {
        fun getCurrentUserSpaceId(): UUID {
            val userDetails = getUserDetailsFromSecurityContext()
            return userDetails.getSpaceId()
        }

        fun getCurrentUserId(): UUID {
            val userDetails = getUserDetailsFromSecurityContext()
            return userDetails.getUserId()
        }

        private fun getUserDetailsFromSecurityContext(): CustomUserDetails {
            val usernamePasswordAuthenticationToken =
                SecurityContextHolder.getContext().authentication as UsernamePasswordAuthenticationToken
            val userDetails = usernamePasswordAuthenticationToken.principal as CustomUserDetails
            return userDetails
        }
    }
}