package org.library.application

import org.library.entity.User

data class UserDto(
    val username: String = "",
    val name: String? = "",
    val email: String = "",
    val id: Long = 0
) {
    companion object {
        fun createFromUser(user: User): UserDto {
            return user.let { UserDto(it.username, it.name, it.email, it.id) }
        }
    }
}