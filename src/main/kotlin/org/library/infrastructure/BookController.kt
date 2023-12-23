package org.library.infrastructure

import org.library.application.BookDTO
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
    fun create(@RequestBody bookDTO: BookDTO) {
        bookService.save(bookDTO)
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
        val bookDTO = bookService.showById(id)

        return if (bookDTO != null) {
            return ResponseEntity.ok(bookDTO)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not exists!")
        }
    }

    @GetMapping
    fun showAll(): List<BookDTO> {
        val books = bookService.findAll()

        return books
    }

    @GetMapping("/borrow")
    fun borrowBook(@RequestBody requestBody: BorrowRequestBody): ResponseEntity<String> {
        val bookId = requestBody.bookId
        val userId = requestBody.userId

        try {
            bookService.borrowBook(bookId, userId)
            return ResponseEntity("Book with ID $bookId borrowed successfully", HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            return ResponseEntity("Book with ID $bookId not found", HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            return ResponseEntity("An error occurred while borrow the book", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    fun returnBook(requestBody: ReturnRequestBody): ResponseEntity<String> {
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