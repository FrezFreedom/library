package org.library.application

import org.library.entity.User
import java.util.*

interface UserRepository {
    fun findById(id: Long): User?

    fun save(user: User): User

    fun findByUsername(username: String?): Optional<User?>?

    fun deleteById(id: Long)

    fun findAll(): List<User>
}