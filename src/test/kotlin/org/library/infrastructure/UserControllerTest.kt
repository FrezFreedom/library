package org.library.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.library.application.*
import org.library.entity.Role
import org.library.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.*

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper,
    private val roleRepository: RoleRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
) {

    @BeforeEach
    fun init() {
        val adminRole = Role(1, "ADMIN")
        roleRepository.save(adminRole)
        val userRole = Role(2, "USER")
        roleRepository.save(userRole)
        val frez = User(1L, "frez", "faraz", "frez@gmail.com", bCryptPasswordEncoder.encode("frez"), setOf(adminRole))
        val khashi =
            User(2L, "khashi", "khashayar", "khashi@gmail.com", bCryptPasswordEncoder.encode("khashi"), setOf(userRole))
        val mojo =
            User(3L, "mojo", "javad", "javad@gmail.com", bCryptPasswordEncoder.encode("mojo"), setOf(userRole))

        userRepository.save(frez)
        userRepository.save(khashi)
        userRepository.save(mojo)
    }

    data class UserCredentials(val username: String, val password: String)

    fun getAdmin(): UserCredentials {
        return UserCredentials("frez", "frez")
    }

    fun getUser1(): UserCredentials {
        return UserCredentials("khashi", "khashi")
    }

    fun getUser2(): UserCredentials {
        return UserCredentials("mojo", "mojo")
    }

    @Test
    @DirtiesContext
    fun `return OK status code when admin user creates a user successfully`() {
        val crudUserDto = CrudUserDto("newUser", "khashayar", "newUser@gmail.com")
        val requestContent = objectMapper.writeValueAsString(crudUserDto)

        val resultPostRequest = mockMvc.post("/api/users") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
            with(httpBasic(getAdmin().username, getAdmin().password))
        }

        resultPostRequest
            .andExpect {
                status { isOk() }
            }

        val user = userRepository.findByUsername("newUser")!!.get()
        Assertions.assertThat(user.let { CrudUserDto(it.username, it.name, it.email) }).isEqualTo(crudUserDto)
    }

    @Test
    @DirtiesContext
    fun `return FORBIDDEN status code when non admin user wants to create a user`() {
        val crudUserDto = CrudUserDto("newUser", "khashayar", "newUser@gmail.com")
        val requestContent = objectMapper.writeValueAsString(crudUserDto)

        val resultPostRequest = mockMvc.post("/api/users") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
            with(httpBasic(getUser1().username, getUser1().password))
        }

        resultPostRequest
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    @DirtiesContext
    fun `return UNAUTHORIZATION status code when unauthorized user wants to create a user`() {
        val crudUserDto = CrudUserDto("newUser", "khashayar", "newUser@gmail.com")
        val requestContent = objectMapper.writeValueAsString(crudUserDto)

        val resultPostRequest = mockMvc.post("/api/users") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
        }

        resultPostRequest
            .andExpect {
                status { isUnauthorized() }
            }
    }

    @Test
    @DirtiesContext
    fun `return OK status code when admin user deletes a user successfully`() {
        val user = User(username = "newUser", name = "newUser", email = "newUser@gmail.com", password = "123456")
        userRepository.save(user)

        val mockedUserId = userRepository.findByUsername("newUser")!!.get().id

        mockMvc.delete("/api/users/$mockedUserId") {
            with(httpBasic(getAdmin().username, getAdmin().password))
        }
            .andExpect {
                status { isOk() }
            }

        assertTrue(userRepository.findByUsername("newUser")!!.isEmpty)
    }

    @Test
    @DirtiesContext
    fun `return FORBIDDEN status code when non admin user wants to deleted a user`() {
        val user = User(username = "newUser", name = "newUser", email = "newUser@gmail.com", password = "123456")
        userRepository.save(user)

        val mockedUserId = userRepository.findByUsername("newUser")!!.get().id

        mockMvc.delete("/api/users/$mockedUserId") {
            with(httpBasic(getUser1().username, getUser1().password))
        }
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    @DirtiesContext
    fun `return UNAUTHORIZED status code when unauthorized user wants to delete a user`() {
        val user = User(username = "newUser", name = "newUser", email = "newUser@gmail.com", password = "123456")
        userRepository.save(user)

        val mockedUserId = userRepository.findByUsername("newUser")!!.get().id

        mockMvc.delete("/api/users/$mockedUserId")
            .andExpect {
                status { isUnauthorized() }
            }
    }

    @Test
    @DirtiesContext
    fun `return OK status and content of specific user when admin user wants to get a user`() {
        val user = User(username = "newUser", name = "newName", email = "newUser@gmail.com", password = "123456")
        userRepository.save(user)

        val mockedUserId = userRepository.findByUsername("newUser")!!.get().id

        mockMvc.get("/api/users/$mockedUserId"){
            with(httpBasic(getAdmin().username, getAdmin().password))
        }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.username") { value(user.username) }
                jsonPath("$.name") { value(user.name) }
                jsonPath("$.email") { value(user.email) }
            }
    }

    @Test
    @DirtiesContext
    fun `return UNAUTHORIZED status and content of specific user when unauthorized user wants to get user`() {
        val user = User(username = "newUser", name = "newName", email = "newUser@gmail.com", password = "123456")
        userRepository.save(user)

        val mockedUserId = userRepository.findByUsername("newUser")!!.get().id

        mockMvc.get("/api/users/$mockedUserId")
            .andExpect {
                status { isUnauthorized() }
            }
    }

    @Test
    @DirtiesContext
    fun `return OK status and content of specific user when non admin requests to get information of himself`() {
        val khashi = userRepository.findByUsername("khashi")!!.get()

        mockMvc.get("/api/users/${khashi.id}"){
            with(httpBasic(getUser1().username, getUser1().password))
        }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.username") { value(khashi.username) }
                jsonPath("$.name") { value(khashi.name) }
                jsonPath("$.email") { value(khashi.email) }
            }
    }

    @Test
    @DirtiesContext
    fun `return FORBIDDEN status when non admin user wants to get information of another person`() {

        val khashi = userRepository.findByUsername("khashi")!!.get()

        mockMvc.get("/api/users/${khashi.id}"){
            with(httpBasic(getUser2().username, getUser2().password))
        }
            .andExpect {
                status { isForbidden() }
            }
    }

    @Test
    @DirtiesContext
    fun `return UNAUTHORIZED status when unauthorized user want to request to get information of a user`() {

        val khashi = userRepository.findByUsername("khashi")!!.get()

        mockMvc.get("/api/users/${khashi.id}")
            .andExpect {
                status { isUnauthorized() }
            }
    }

    @Test
    @DirtiesContext
    fun `return OK status when admin user updates a user successfully`() {
        val userId = 1L
        val updateCrudUserDto = CrudUserDto("frezUpdated", "farazUpdated", "frez@gmail.comUpdated", "123456Updated")
        val requestContent = objectMapper.writeValueAsString(updateCrudUserDto)

        val resultPutRequest = mockMvc.put("/api/users/$userId") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
            with(httpBasic(getAdmin().username, getAdmin().password))
        }

        resultPutRequest
            .andExpect {
                status { isOk() }
            }

        val user = userRepository.findAll().first()

        assertEquals(updateCrudUserDto.name, user.name)
        assertEquals(updateCrudUserDto.username, user.username)
        assertEquals(updateCrudUserDto.email, user.email)
        assertTrue(BCryptPasswordEncoder().matches(updateCrudUserDto.password, user.password))
    }

    @Test
    @DirtiesContext
    fun `return UNAUTHORIZED status when unauthorized user wants to update a user`() {
        val userId = 1L
        val updateCrudUserDto = CrudUserDto("frezUpdated", "farazUpdated", "frez@gmail.comUpdated", "123456Updated")
        val requestContent = objectMapper.writeValueAsString(updateCrudUserDto)

        val resultPutRequest = mockMvc.put("/api/users/$userId") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
        }

        resultPutRequest
            .andExpect {
                status { isUnauthorized() }
            }
    }

    @Test
    @DirtiesContext
    fun `return NO_FOUND status when user id is not valid`() {
        val userId = 100L
        val updateCrudUserDto = CrudUserDto("frezUpdated", "farazUpdated", "frez@gmail.comUpdated", "123456Updated")
        val requestContent = objectMapper.writeValueAsString(updateCrudUserDto)

        val resultPutRequest = mockMvc.put("/api/users/$userId") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
            with(httpBasic(getAdmin().username, getAdmin().password))
        }

        resultPutRequest
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @DirtiesContext
    fun `return OK status when non admin user updates information of himself successfully`() {
        val userId = userRepository.findByUsername("mojo")!!.get().id
        val updateCrudUserDto = CrudUserDto("mojoUpdated", "mojoUpdated", "mojo@gmail.comUpdated", "123456Updated")
        val requestContent = objectMapper.writeValueAsString(updateCrudUserDto)

        val resultPutRequest = mockMvc.put("/api/users/$userId") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
            with(httpBasic(getUser2().username, getUser2().password))
        }

        resultPutRequest
            .andExpect {
                status { isOk() }
            }

        val user = userRepository.findByUsername("mojoUpdated")!!.get()

        assertEquals(updateCrudUserDto.name, user.name)
        assertEquals(updateCrudUserDto.username, user.username)
        assertEquals(updateCrudUserDto.email, user.email)
        assertTrue(BCryptPasswordEncoder().matches(updateCrudUserDto.password, user.password))
    }

    @Test
    @DirtiesContext
    fun `return FORBIDDEN status when non admin user wants to update information of another user`() {
        val userId = userRepository.findByUsername("mojo")!!.get().id
        val updateCrudUserDto = CrudUserDto("mojoUpdated", "mojoUpdated", "mojo@gmail.comUpdated", "123456Updated")
        val requestContent = objectMapper.writeValueAsString(updateCrudUserDto)

        val resultPutRequest = mockMvc.put("/api/users/$userId") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
            with(httpBasic(getUser1().username, getUser1().password))
        }

        resultPutRequest
            .andExpect {
                status { isForbidden() }
            }
    }
}