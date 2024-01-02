package org.library.application

import org.library.entity.User

data class CrudUserDto(
    val username: String = "",
    val name: String? = "",
    val email: String = "",
    val password: String = ""
) {
    companion object {
        fun createFromUser(user: User): CrudUserDto {
            return user.let { CrudUserDto(it.username, it.name, it.email, it.password) }
        }
    }
}