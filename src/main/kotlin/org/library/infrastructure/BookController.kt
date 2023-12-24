package org.library.infrastructure

import org.library.application.BookDTO
import org.library.application.BookNotAvailableException
import org.library.application.BookService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/books")
class BookController(private val bookService: BookService) {

    @PostMapping
    fun create(@RequestBody bookDTO: BookDTO): ResponseEntity<String> {
        try {
            bookService.save(bookDTO)
            return ResponseEntity("Book created successfully", HttpStatus.OK)
        } catch (exception: Exception) {
            return ResponseEntity("An error occurred while deleting the book", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @DeleteMapping
    fun delete(@RequestBody requestBody: DeleteRequestBody): ResponseEntity<String> {
        try {
            bookService.deleteById(requestBody.id)
            return ResponseEntity("Book with ID ${requestBody.id} deleted successfully", HttpStatus.OK)
        } catch (exception: NoSuchElementException) {
            return ResponseEntity("Book with ID ${requestBody.id} not found", HttpStatus.NOT_FOUND)
        } catch (exception: Exception) {
            return ResponseEntity("An error occurred while deleting the book", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/{id}")
    fun show(@PathVariable id: UUID): ResponseEntity<Any> {
        try {
            val bookDTO = bookService.showById(id)

            return if (bookDTO != null) {
                ResponseEntity(bookDTO, HttpStatus.OK)
            } else {
                ResponseEntity("Book not exists!", HttpStatus.NOT_FOUND)
            }
        } catch (exception: Exception) {
            return ResponseEntity("An error occurred while show the book", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping
    fun showAll(): ResponseEntity<Any> {
        try {
            val books = bookService.findAll()

            return ResponseEntity(books, HttpStatus.OK)
        } catch (exception: Exception) {
            return ResponseEntity("An error occurred while show all books", HttpStatus.INTERNAL_SERVER_ERROR)
        }

    }

    @PostMapping("/borrow")
    fun borrowBook(@RequestBody requestBody: BorrowRequestBody): ResponseEntity<String> {
        val bookId = requestBody.bookId
        val userId = requestBody.userId

        try {
            bookService.borrowBook(bookId, userId)
            return ResponseEntity("Book with ID $bookId borrowed successfully", HttpStatus.OK)
        } catch (e: BookNotAvailableException) {
            return ResponseEntity("Book with ID $bookId is not available now!", HttpStatus.BAD_REQUEST)
        } catch (e: NoSuchElementException) {
            return ResponseEntity("Book with ID $bookId or User with ID $userId not found", HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            return ResponseEntity("An error occurred while borrow the book", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PostMapping("/return")
    fun returnBook(@RequestBody requestBody: ReturnRequestBody): ResponseEntity<String> {
        val bookId = requestBody.bookId

        try {
            bookService.returnBook(bookId)
            return ResponseEntity("Book with ID $bookId returned successfully", HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            return ResponseEntity("Book with ID $bookId not found", HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            return ResponseEntity("An error occurred while return the book", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}