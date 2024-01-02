package org.library.security

import org.library.application.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service


@Service
class UserDetailsServiceImpl @Autowired constructor(val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetailsWithId {
        val user = userRepository.findByUsername(username)?.get() ?: throw Exception()

        val authorities: Collection<GrantedAuthority> = user.roles!!.map { role ->
            SimpleGrantedAuthority("ROLE_${role.name}")
        }

        return UserDetailsWithId(
            user.id,
            user.username,
            user.password,
            authorities
        )
    }
}
