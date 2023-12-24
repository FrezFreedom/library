package org.library.application


import org.library.entity.User

interface UserRepository {
    fun findById(id: Long): User?

    fun save(user: User): User
}