package org.library.infrastructure

import org.library.application.BookRepository
import org.library.application.BookService
import org.library.entity.Book
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/books")
class BookController(private val bookService: BookService) {

    @PostMapping
    fun create(@RequestBody book: Book): Book {
        return bookService.save(book)
    }
}