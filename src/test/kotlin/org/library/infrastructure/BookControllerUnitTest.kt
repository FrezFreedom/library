package org.library.infrastructure

import io.mockk.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.library.application.BookService
import org.library.application.ElementNotFoundException
import java.util.UUID
import kotlin.random.Random

class BookControllerUnitTest {

    @Nested
    @DisplayName("POST /api/books/borrow")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class BorrowBook {
        @Test
        fun `should return status OK when the book borrowed successfully`() {
            val bookService = mockk<BookService>()
            val userId = Random.nextLong()
            val bookId = UUID.randomUUID()
            val requestBody = BorrowRequestBody(bookId, userId)
            every { bookService.borrowBook(bookId, userId) } just runs
            val bookController = BookController(bookService)

            bookController.borrowBook(requestBody)

            verify { bookService.borrowBook(bookId, userId) }
        }

        @Test
        fun `should return status NOT_FOUND when the book id is invalid`() {
            val bookService = mockk<BookService>()
            val bookController = BookController(bookService)
            val userId = Random.nextLong()
            val bookId = UUID.randomUUID()
            val requestBody = BorrowRequestBody(bookId, userId)
            every { bookService.borrowBook(bookId, userId) } throws ElementNotFoundException()

            assertThrows(ElementNotFoundException::class.java) {
                bookController.borrowBook(requestBody)
            }
        }

        @Test
        fun `should return INTERNAL_SERVER_ERROR status when occurs problem`() {
            val bookService = mockk<BookService>()
            val bookController = BookController(bookService)
            val userId = Random.nextLong(1, Long.MAX_VALUE)
            val bookId = UUID.randomUUID()
            val requestBody = BorrowRequestBody(bookId, userId)
            every { bookService.borrowBook(bookId, userId) } throws Exception()

            assertThrows(Exception::class.java) {
                bookController.borrowBook(requestBody)
            }
        }
    }

    @Nested
    @DisplayName("POST /api/books/return")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class ReturnBook {
        @Test
        fun `should return status OK when return book requested correctly`() {
            val bookService = mockk<BookService>()
            val bookController = BookController(bookService)
            val bookId = UUID.randomUUID()
            every { bookService.returnBook(bookId) } just runs
            val requestBody = BookReturnRequestBody(bookId)

            bookController.returnBook(requestBody)

            verify { bookService.returnBook(bookId) }
        }

        @Test
        fun `should return NOT_FOUND status when want to return invalid book`() {
            val bookService = mockk<BookService>()
            val bookController = BookController(bookService)
            val bookId = UUID.randomUUID()
            every { bookService.returnBook(bookId) } throws ElementNotFoundException()
            val requestBody = BookReturnRequestBody(bookId)

            assertThrows(ElementNotFoundException::class.java) {
                bookController.returnBook(requestBody)
            }
            verify { bookService.returnBook(bookId) }
        }

        @Test
        fun `should return INTERNAL_SERVER_ERROR status when occurs problem`(){
            val bookService = mockk<BookService>()
            val bookController = BookController(bookService)
            val bookId = UUID.randomUUID()
            every { bookService.returnBook(bookId) } throws Exception()
            val requestBody = BookReturnRequestBody(bookId)

            assertThrows(Exception::class.java) {
                bookController.returnBook(requestBody)
            }
            verify { bookService.returnBook(bookId) }
        }
    }
}