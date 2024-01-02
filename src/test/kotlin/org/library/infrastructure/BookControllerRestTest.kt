package org.library.infrastructure

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.MockKGateway.VerificationResult.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.library.application.*
import org.library.entity.Book
import org.library.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
//import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.util.UUID
import kotlin.random.Random


@SpringBootTest
@AutoConfigureMockMvc
class BookControllerRestTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
) {

    private val objectMapper = ObjectMapper()

    @BeforeEach
    fun init() {
//        val crudUserDto = CrudUserDto("frez", "faraz", "frez@gmail.com", "12345678")
//        userService.save(crudUserDto)
//        val user = User("frez", "faraz", "frez@gmail.com", "12345678", 1L)
//        userRepository.save(user)
    }


    @Test
    @DirtiesContext
    fun `return OK status code when create successful book`() {
        val bookDTO = BookDTO("title", "isbn")
        val requestContent = objectMapper.writeValueAsString(bookDTO)

        val resultPostRequest = mockMvc.post("/api/books") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
//            with(httpBasic("frez", "12345678"))
        }

        resultPostRequest
            .andExpect {
                status { isOk() }
            }

        val book = bookRepository.findAll().first()
        assertThat(book.let { BookDTO(it.title, it.isbn) }).isEqualTo(bookDTO)
    }

    @Test
    @DirtiesContext
    fun `return OK status code when book deleted successfully`() {
        val randomUUID = UUID.randomUUID()
        val book = Book(randomUUID, "title", "isbn")
        bookRepository.save(book)
        val deleteRequestBody = DeleteRequestBody(randomUUID)
        val requestContent = objectMapper.writeValueAsString(deleteRequestBody)

        val resultDeleteRequest = mockMvc.delete("/api/books") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
        }

        resultDeleteRequest
            .andExpect {
                status { isOk() }
            }

        val books = bookRepository.findAll()
        Assertions.assertEquals(books.size, 0)
    }

    @Test
    @DirtiesContext
    fun `return OK status and content of specific book when request it`() {
        val randomUUID = UUID.randomUUID()
        val sampleTitle = "title"
        val sampleIsbn = "isbn"
        val book = Book(randomUUID, sampleTitle, sampleIsbn)
        bookRepository.save(book)

        mockMvc.get("/api/books/$randomUUID")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.title") { value(sampleTitle) }
                jsonPath("$.isbn") { value(sampleIsbn) }
                jsonPath("$.id") { value(randomUUID.toString()) }
            }
    }

    @Test
    @DirtiesContext
    fun `return NOT_FOUND status when request to get invalid id book`() {
        val randomUUID = UUID.randomUUID()
        val sampleTitle = "title"
        val sampleIsbn = "isbn"
        val book = Book(randomUUID, sampleTitle, sampleIsbn)
        bookRepository.save(book)

        val invalidRandomUUID = UUID.randomUUID()

        mockMvc.get("/api/books/$invalidRandomUUID")
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @DirtiesContext
    fun `return list all books when request to get all books`() {
        val expectedBookDTOS = listOf(
            BookDTO("title_a", "isbn_a", UUID.randomUUID()),
            BookDTO("title_b", "isbn_b", UUID.randomUUID()),
            BookDTO("title_c", "isbn_c", UUID.randomUUID()),
        )

        expectedBookDTOS.forEach { bookDTO ->
            bookRepository.save(Book(bookDTO.id!!, bookDTO.title, bookDTO.isbn))
        }

        val responseBookDTOs =
            mockMvc.get("/api/books")
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                }.andReturn().response.contentAsString

        val resultBookDTOs: List<BookDTO> =
            objectMapper.readValue(responseBookDTOs, object : TypeReference<List<BookDTO>>() {})

        assertThat(resultBookDTOs).containsExactlyInAnyOrderElementsOf(expectedBookDTOS)
    }

    @Test
    @DirtiesContext
    fun `return OK status when return book to library successfully`() {
        val user = User(1L,"frez", "faraz", "frez@gmail.com", "####")
        userRepository.save(user)
        val randomUUID = UUID.randomUUID()
        val book = Book(randomUUID, "title", "isbn", user)
        bookRepository.save(book)
        val bookReturnRequestBody = BookReturnRequestBody(randomUUID)
        val requestContent = objectMapper.writeValueAsString(bookReturnRequestBody)


        val resultPostRequest = mockMvc.post("/api/books/return") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
        }

        resultPostRequest
            .andDo { print() }
            .andExpect {
                status { isOk() }
            }

        val bookAfterAction = bookRepository.showById(randomUUID)
        Assertions.assertEquals(null, bookAfterAction?.user)
    }

    @Test
    @DirtiesContext
    fun `return NOT_FOUND status when return invalid book to library`() {
        val randomUUID = UUID.randomUUID()
        val book = Book(randomUUID, "title", "isbn")
        bookRepository.save(book)
        val invalidUUID = UUID.randomUUID()
        val bookReturnRequestBody = BookReturnRequestBody(invalidUUID)
        val requestContent = objectMapper.writeValueAsString(bookReturnRequestBody)


        val resultPostRequest = mockMvc.post("/api/books/return") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
        }

        resultPostRequest
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @DirtiesContext
    fun `return OK status when borrow the book to user successfully`() {
        val user = User(1L,"frez", "faraz", "frez@gmail.com", "####")
        userRepository.save(user)
        val randomUUID = UUID.randomUUID()
        val book = Book(randomUUID, "tile", "isbn")
        bookRepository.save(book)
        val borrowRequestBody = BorrowRequestBody(randomUUID, 1L)
        val requestContent = objectMapper.writeValueAsString(borrowRequestBody)
        val expectedUser = userRepository.findById(1L)

        val resultPostRequest = mockMvc.post("/api/books/borrow") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
        }

        resultPostRequest
            .andDo { print() }
            .andExpect {
                status { isOk() }
            }
        val bookAfterAction = bookRepository.showById(randomUUID)
        Assertions.assertNotNull(bookAfterAction?.user)
        Assertions.assertEquals(expectedUser?.id, bookAfterAction?.user?.id)
    }

    @Test
    @DirtiesContext
    fun `return NO_FOUND status when want to borrow to invalid user`() {
        val user = User(1L,"frez", "faraz", "frez@gmail.com", "####")
        userRepository.save(user)
        val randomUUID = UUID.randomUUID()
        val book = Book(randomUUID, "tile", "isbn")
        bookRepository.save(book)
        val invalidUserId = Random.nextLong(2, Long.MAX_VALUE)
        val borrowRequestBody = BorrowRequestBody(randomUUID, invalidUserId)
        val requestContent = objectMapper.writeValueAsString(borrowRequestBody)

        val resultPostRequest = mockMvc.post("/api/books/borrow") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
        }

        resultPostRequest
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @DirtiesContext
    fun `return NO_FOUND status when want to borrow a invalid book`() {
        val user = User(1L,"frez", "faraz", "frez@gmail.com", "####")
        userRepository.save(user)
        val randomUUID = UUID.randomUUID()
        val book = Book(randomUUID, "tile", "isbn")
        bookRepository.save(book)
        val invalidBookId = UUID.randomUUID()
        val borrowRequestBody = BorrowRequestBody(invalidBookId, 1L)
        val requestContent = objectMapper.writeValueAsString(borrowRequestBody)

        val resultPostRequest = mockMvc.post("/api/books/borrow") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
        }

        resultPostRequest
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    @DirtiesContext
    fun `return BAD_REQUEST status when want to borrow not available book`() {
        val user = User(1L,"frez", "faraz", "frez@gmail.com", "####")
        val userWithBook = User(2L,"frez2", "faraz", "frez2@gmail.com", "####")
        userRepository.save(user)
        userRepository.save(userWithBook)
        val randomUUID = UUID.randomUUID()
        val userWithBookLoadedFromDatabase = userRepository.findById(2L)
        val book = Book(randomUUID, "tile", "isbn", userWithBookLoadedFromDatabase)
        bookRepository.save(book)
        val borrowRequestBody = BorrowRequestBody(randomUUID, 1L)
        val requestContent = objectMapper.writeValueAsString(borrowRequestBody)

        val resultPostRequest = mockMvc.post("/api/books/borrow") {
            contentType = MediaType.APPLICATION_JSON
            content = requestContent
        }

        resultPostRequest
            .andDo { print() }
            .andExpect {
                status { isBadRequest() }
            }
    }

}