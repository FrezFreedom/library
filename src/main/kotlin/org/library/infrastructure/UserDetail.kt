package org.library.infrastructure

import org.library.application.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service


@Service
class UserDetail @Autowired constructor(val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)?.get() ?: throw Exception()

        return User.builder()
            .username(username)
            .password(user.password)
            .roles(*user.roles!!.map { it.name }.toTypedArray())
            .build()
    }
}
