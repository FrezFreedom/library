package org.library.security

import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class BCryptPasswordEncoderProvider {
    @Bean
    fun provideBCryptPasswordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()
}