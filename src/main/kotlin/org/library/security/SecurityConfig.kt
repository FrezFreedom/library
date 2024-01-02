package org.library.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
class SecurityConfig {

    @Bean
    @Throws(Exception::class)
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain? {

        return httpSecurity
            .csrf { it.disable() }
            .authorizeHttpRequests { authorize ->
                // api/users
                authorize.requestMatchers("/api/users").hasRole("ADMIN")
                authorize.requestMatchers(HttpMethod.DELETE,"/api/users/{id}").hasRole("ADMIN")
                authorize.requestMatchers(HttpMethod.GET,"/api/users/{id}").access(GetUserAuthorizationManager())
                authorize.requestMatchers(HttpMethod.PUT,"/api/users/{id}").access(GetUserAuthorizationManager())
                // api/books
                authorize.requestMatchers(HttpMethod.GET,"/api/books").authenticated()
                authorize.requestMatchers(HttpMethod.POST,"/api/books").hasRole("ADMIN")
                authorize.requestMatchers(HttpMethod.DELETE,"/api/books").hasRole("ADMIN")
                authorize.requestMatchers(HttpMethod.GET,"/api/books/{id}").authenticated()
                authorize.requestMatchers(HttpMethod.POST,"/api/books/return", "/api/books/borrow").hasRole("ADMIN")
            }
            .httpBasic { }
            .build()
    }
}
