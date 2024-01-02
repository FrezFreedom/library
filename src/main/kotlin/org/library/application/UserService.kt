package org.library.application

import org.library.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) {
    fun showById(userId: Long): UserDto {
        val user = userRepository.findById(userId) ?: throw ElementNotFoundException("User with ID $userId not found")
        return UserDto.createFromUser(user)
    }

    fun save(crudUserDto: CrudUserDto) {
        val encryptedPassword = BCryptPasswordEncoder().encode(crudUserDto.password)
        val user = crudUserDto.let {
            User(
                username = it.username,
                name = it.name,
                email = it.email,
                password = encryptedPassword
            )
        }
        userRepository.save(user)
    }

    fun deleteById(userId: Long) {
        userRepository.findById(userId) ?: throw ElementNotFoundException("User with ID $userId not found")

        userRepository.deleteById(userId)
    }

    fun findAll(): List<UserDto> {
        val users = userRepository.findAll()

        return users.map { UserDto.createFromUser(it) }
    }

    fun update(userId: Long, crudUserDto: CrudUserDto) {
        val user = userRepository.findById(userId) ?: throw ElementNotFoundException("User with ID $userId not found")

        user.name = crudUserDto.name
        user.email = crudUserDto.email
        user.username = crudUserDto.username
        user.password = bCryptPasswordEncoder.encode(crudUserDto.password)

        userRepository.save(user)
    }
}