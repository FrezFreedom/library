package org.library.infrastructure

import io.mockk.*
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestInstance
import org.library.application.BookService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID

class BookControllerTest {

    @Nested
    @DisplayName("POST /api/books/borrow")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class BorrowBook {
        @Test
        fun `should return status OK when the book borrowed successfully`() {
            val bookService = mockk<BookService>()
            val userId = 5L
            val bookId = UUID.fromString("24269be5-af13-4545-a37e-ca0cf2d7657b")
            val requestBody = BorrowRequestBody(bookId, userId)
            every { bookService.borrowBook(bookId, userId) } just runs
            val bookController = BookController(bookService)
            val expectedResult = ResponseEntity("Book with ID $bookId borrowed successfully", HttpStatus.OK)

            val result = bookController.borrowBook(requestBody)

            assertEquals(expectedResult, result)
        }

        @Test
        fun `should return status NOT_FOUND when the book id is invalid`() {
            val bookService = mockk<BookService>()
            val userId = 5L
            val bookId = UUID.fromString("24269be5-af13-4545-a37e-ca0cf2d7657b")
            val requestBody = BorrowRequestBody(bookId, userId)
            every { bookService.borrowBook(bookId, userId) } throws NoSuchElementException()
            val bookController = BookController(bookService)
            val expectedResult = ResponseEntity("Book with ID $bookId not found", HttpStatus.NOT_FOUND)

            val result = bookController.borrowBook(requestBody)

            assertEquals(expectedResult, result)
        }

        @Test
        fun `should return INTERNAL_SERVER_ERROR status when occurs problem`() {
            val bookService = mockk<BookService>()
            val userId = 5L
            val bookId = UUID.fromString("24269be5-af13-4545-a37e-ca0cf2d7657b")
            val requestBody = BorrowRequestBody(bookId, userId)
            every { bookService.borrowBook(bookId, userId) } throws Exception()
            val bookController = BookController(bookService)
            val expectedResult = ResponseEntity("An error occurred while borrow the book", HttpStatus.INTERNAL_SERVER_ERROR)

            val result = bookController.borrowBook(requestBody)

            assertEquals(expectedResult, result)
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
            val requestBody = ReturnRequestBody(bookId)
            val expectedResult = ResponseEntity("Book with ID $bookId returned successfully", HttpStatus.OK)

            val result = bookController.returnBook(requestBody)

            verify { bookService.returnBook(bookId) }
            assertEquals(expectedResult, result)
        }

        @Test
        fun `should return NOT_FOUND status when want to return invalid book`() {
            val bookService = mockk<BookService>()
            val bookController = BookController(bookService)
            val bookId = UUID.randomUUID()
            every { bookService.returnBook(bookId) } throws NoSuchElementException()
            val requestBody = ReturnRequestBody(bookId)
            val expectedResult = ResponseEntity("Book with ID $bookId not found", HttpStatus.NOT_FOUND)

            val result = bookController.returnBook(requestBody)

            verify { bookService.returnBook(bookId) }
            assertEquals(expectedResult, result)
        }

        @Test
        fun `should return INTERNAL_SERVER_ERROR status when occurs problem`(){
            val bookService = mockk<BookService>()
            val bookController = BookController(bookService)
            val bookId = UUID.randomUUID()
            every { bookService.returnBook(bookId) } throws Exception()
            val requestBody = ReturnRequestBody(bookId)
            val expectedResult = ResponseEntity("An error occurred while return the book", HttpStatus.INTERNAL_SERVER_ERROR)

            val result = bookController.returnBook(requestBody)

            verify { bookService.returnBook(bookId) }
            assertEquals(expectedResult, result)
        }
    }
}