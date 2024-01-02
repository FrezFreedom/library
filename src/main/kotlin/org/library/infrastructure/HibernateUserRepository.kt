package org.library.infrastructure

import org.library.application.UserRepository
import org.library.entity.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface HibernateUserRepository : UserRepository, CrudRepository<User?, Long?> {
    override fun findByUsername(username: String?): Optional<User?>?
}