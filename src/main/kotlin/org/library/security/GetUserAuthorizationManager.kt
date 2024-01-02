package org.library.security

import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import java.util.function.Supplier

class GetUserAuthorizationManager() : AuthorizationManager<RequestAuthorizationContext> {

    override fun check(
        authentication: Supplier<Authentication>?,
        `object`: RequestAuthorizationContext?
    ): AuthorizationDecision {
        val auth = SecurityContextHolder.getContext().authentication

        if (auth is AnonymousAuthenticationToken)
            return AuthorizationDecision(false)

        val idOfUserThatRequested = (auth.principal as UserDetailsWithId).getId()
        val requestedId = `object`?.variables?.get("id")?.toLong()

        if (idOfUserThatRequested == requestedId)
            return AuthorizationDecision(true)

        if (auth.authorities.any { authority ->
                authority.authority == "ROLE_ADMIN"
            }) {
            return AuthorizationDecision(true)
        }

        return AuthorizationDecision(false)
    }
}