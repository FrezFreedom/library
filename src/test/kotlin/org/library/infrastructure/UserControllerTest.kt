package org.library.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.library.application.BookDTO
import org.library.application.CrudUserDto
import org.library.application.UserRepository
import org.library.application.UserService
import org.library.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.*

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val userRepository: UserRepository,
    private val objectMapper: ObjectMapper,
    private val userService: UserService,
) {

    @Test
    @DirtiesContext
    fun `return OK status code when create successful user`() {
        val crudUserDto = CrudUserDto("khashi", "khashayar", "123456")
        val requestContent = objectMapper.writeValueAsString(crudUserDto)

        val resultPostRequest = mockMvc.post("/api/users") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
        }

        resultPostRequest
            .andExpect {
                status { isOk() }
            }

        val user = userRepository.findAll().first()
        Assertions.assertThat(user.let { CrudUserDto(it.username, it.name, it.email) }).isEqualTo(crudUserDto)
    }

    @Test
    @DirtiesContext
    fun `return OK status code when book deleted successfully`(){
        val mockedUserId = 1L
        val user = User(mockedUserId, "frez", "faraz", "frez@gmail.com","123456")
        userRepository.save(user)

        mockMvc.delete("/api/users/$mockedUserId")
            .andExpect {
                status { isOk() }
            }

        val users = userRepository.findAll()
        assertEquals(0, users.size)
    }

    @Test
    @DirtiesContext
    fun `return OK status and content of specific user when request it`(){
        val mockedUserId = 1L
        val user = User(mockedUserId, "frez", "faraz", "frez@gmail.com","123456")
        userRepository.save(user)

        mockMvc.get("/api/users/$mockedUserId")
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.username") {value(user.username)}
                jsonPath("$.name") {value(user.name)}
                jsonPath("$.email") {value(user.email)}
            }
    }

    @Test
    @DirtiesContext
    fun `return OK status when update successfully`() {
        val mockedCrudUserDto = CrudUserDto("frez", "faraz", "frez@gmail.com","123456")
        userService.save(mockedCrudUserDto)

        val userId = 1L
        val updateCrudUserDto = CrudUserDto("frezUpdated", "farazUpdated", "frez@gmail.comUpdated", "123456Updated")
        val requestContent = objectMapper.writeValueAsString(updateCrudUserDto)

        val resultPutRequest = mockMvc.put("/api/users/$userId") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
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
    fun `return NO_FOUND status when user id is not valid`(){
        val userId = 1L
        val updateCrudUserDto = CrudUserDto("frezUpdated", "farazUpdated", "frez@gmail.comUpdated", "123456Updated")
        val requestContent = objectMapper.writeValueAsString(updateCrudUserDto)

        val resultPutRequest = mockMvc.put("/api/users/$userId") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
        }

        resultPutRequest
            .andExpect {
                status { isNotFound() }
            }
    }
}