package org.library.infrastructure

import org.library.application.BookDTO
import org.library.application.BookService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/books")
@RestControllerAdvice
class BookController @Autowired constructor(private val bookService: BookService) {

    @PostMapping
    fun create(@RequestBody bookDTO: BookDTO) =
        bookService.save(bookDTO)

    @DeleteMapping
    fun delete(@RequestBody requestBody: DeleteRequestBody) =
        bookService.deleteById(requestBody.id)

    @GetMapping("/{id}")
    fun show(@PathVariable id: UUID) =
        bookService.showById(id)


    @GetMapping
    fun showAll() =
        bookService.findAll()

    @PostMapping("/borrow")
    fun borrowBook(@RequestBody requestBody: BorrowRequestBody) {
        val bookId = requestBody.bookId
        val userId = requestBody.userId

        bookService.borrowBook(bookId, userId)
    }

    @PostMapping("/return")
    fun returnBook(@RequestBody requestBody: BookReturnRequestBody) {
        val bookId = requestBody.bookId

        bookService.returnBook(bookId)
    }
}