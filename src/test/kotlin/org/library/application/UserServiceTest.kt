package org.library.application

import io.mockk.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.library.entity.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

class UserServiceTest {
    @Test
    fun `should show user when show function invokes`() {
        val mockedUserRepository = mockk<UserRepository>()
        val mockedBCryptPasswordEncoder = mockk<BCryptPasswordEncoder>()
        val userDto = UserDto("frez", "faraz", "frez@gmail.com", 1L)
        val mockedUser = User(1L, "frez", "faraz", "frez@gmail.com", "####")
        every { mockedUserRepository.findById(1L) } returns mockedUser
        val userService = UserService(mockedUserRepository, mockedBCryptPasswordEncoder)

        val result = userService.showById(1L)

        Assertions.assertEquals(userDto, result)
    }

    @Test
    fun `should throw exception when showById function invokes with invalid user id`(){
        val mockUserRepository = mockk<UserRepository>()
        val mockedBCryptPasswordEncoder = mockk<BCryptPasswordEncoder>()
        val userId = 1L
        every { mockUserRepository.findById(userId) } throws NoSuchElementException()
        val userService = UserService(mockUserRepository, mockedBCryptPasswordEncoder)

        Assertions.assertThrows(NoSuchElementException::class.java) {
            userService.showById(userId)
        }
        verify { mockUserRepository.findById(userId) }
    }

    @Test
    fun `should create user when create function invokes`() {
        val mockedUserRepository = mockk<UserRepository>()
        val mockedBCryptPasswordEncoder = mockk<BCryptPasswordEncoder>()
        val mockedCrudUserDto = CrudUserDto("khashi", "khashayar", "khashi@gmail.com", "abc123")
        val encryptedPassword = BCryptPasswordEncoder().encode(mockedCrudUserDto.password)
        val user = mockedCrudUserDto.let {
            User(
                username = it.username,
                name = it.name,
                email = it.email,
                password = encryptedPassword
            )
        }
        val userSlot = slot<User>()
        every { mockedUserRepository.save(capture(userSlot)) } returns mockk()
        val userService = UserService(mockedUserRepository, mockedBCryptPasswordEncoder)

        userService.save(mockedCrudUserDto)

        Assertions.assertEquals(user.username, userSlot.captured.username)
        Assertions.assertEquals(user.name, userSlot.captured.name)
        Assertions.assertEquals(user.email, userSlot.captured.email)
//        Assertions.assertEquals(user.password, userSlot.captured.password)
    }

    @Test
    fun `should return true when delete book invokes`() {
        val userId = 1L
        val mockUserRepository = mockk<UserRepository>()
        val mockedBCryptPasswordEncoder = mockk<BCryptPasswordEncoder>()
        every { mockUserRepository.findById(userId) } returns mockk()
        every { mockUserRepository.deleteById(userId) } just runs
        val userService = UserService(mockUserRepository, mockedBCryptPasswordEncoder)

        userService.deleteById(userId)

        verify { mockUserRepository.deleteById(userId) }
        verify { mockUserRepository.findById(userId) }
    }

    @Test
    fun `should return list of users when findAll invokes`() {
        val mockUserRepository = mockk<UserRepository>()
        val mockedBCryptPasswordEncoder = mockk<BCryptPasswordEncoder>()
        val userService = UserService(mockUserRepository, mockedBCryptPasswordEncoder)
        val users = listOf(
            User(username = "khashi", name = "khashayar", email = "khashi@gmail.com", password = "!@#123"),
            User(username = "alisls", name = "alireza", email = "alireza@gmail.com", password = "@#$123")
        )
        val expectedUsers = users.map { UserDto.createFromUser(it) }
        every { mockUserRepository.findAll() } returns users

        val result = userService.findAll()

        expectedUsers.forEachIndexed{index, expectedUser ->
            Assertions.assertEquals(expectedUser.username, result[index].username)
            Assertions.assertEquals(expectedUser.name, result[index].name)
            Assertions.assertEquals(expectedUser.email, result[index].email)
        }
    }

    @Test
    fun `should update user when update function invokes`(){
        val mockUserRepository = mockk<UserRepository>()
        val mockedBCryptPasswordEncoder = mockk<BCryptPasswordEncoder>()
        val userService = UserService(mockUserRepository, mockedBCryptPasswordEncoder)
        val mockedUserToReturn = User(1L, "frez", "faraz", "frez@gmail.com", "!@#123")
        every { mockUserRepository.findById(1L) } returns mockedUserToReturn
        val mockCrudUserDto = CrudUserDto("updatedFrez", "updatedFaraz", "updateFrez@gmail.com", "updated!@#123")
        val userSlot = slot<User>()
        every { mockUserRepository.save(capture(userSlot)) } returns mockk()
        every { mockedBCryptPasswordEncoder.encode("updated!@#123") } returns "\$2a\$12\$Ivi4M.DyLf3KI75vbMYTPu6qJ3iZoo/0D7KK2VfDLooWWxJIl2lia"

        userService.update(1L, mockCrudUserDto)

        Assertions.assertEquals(1L, userSlot.captured.id)
        Assertions.assertEquals(mockCrudUserDto.name, userSlot.captured.name)
        Assertions.assertEquals(mockCrudUserDto.email, userSlot.captured.email)
        Assertions.assertEquals(mockCrudUserDto.username, userSlot.captured.username)
        Assertions.assertTrue(BCryptPasswordEncoder().matches(mockCrudUserDto.password, userSlot.captured.password))
    }
}