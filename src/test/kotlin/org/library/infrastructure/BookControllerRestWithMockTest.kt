package org.library.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.junit.jupiter.api.Test
import org.library.application.BookDTO
import org.library.application.BookService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*
import kotlin.NoSuchElementException

class BookControllerRestWithMockTest {
    private val objectMapper = ObjectMapper()
    private val bookService = mockk<BookService>()
    private val bookController = BookController(bookService)
    private val mockMvc = MockMvcBuilders.standaloneSetup(bookController).build()

    @Test
    fun `return OK status code when create successful book`() {
        val bookDTO = BookDTO("title", "isbn")
        every { bookService.save(bookDTO) } just runs
        val requestContent = objectMapper.writeValueAsString(bookDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent)
        ).andExpect(status().isOk)
    }

    @Test
    fun `return INTERNAL_SERVER_ERROR status code when creation of book fails`() {
        val bookDTO = BookDTO("title", "isbn")
        every { bookService.save(bookDTO) } throws Exception()
        val requestContent = objectMapper.writeValueAsString(bookDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent)
        ).andExpect(status().isInternalServerError)
    }

    @Test
    fun `return OK status code when book deleted successfully`() {
        val requestBody = DeleteRequestBody(UUID.randomUUID())
        every { bookService.deleteById(requestBody.id) } just runs
        val requestContent = objectMapper.writeValueAsString(requestBody)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent)
        ).andExpect(status().isOk)
    }

    @Test
    fun `return NOT_FOUND status code when book id is invalid`() {
        val requestBody = DeleteRequestBody(UUID.randomUUID())
        every { bookService.deleteById(requestBody.id) } throws NoSuchElementException()
        val requestContent = objectMapper.writeValueAsString(requestBody)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent)
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `return INTERNAL_SERVER_ERROR status code when book saving failed`() {
        val requestBody = DeleteRequestBody(UUID.randomUUID())
        every { bookService.deleteById(requestBody.id) } throws Exception()
        val requestContent = objectMapper.writeValueAsString(requestBody)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent)
        ).andExpect(status().isInternalServerError)
    }
}