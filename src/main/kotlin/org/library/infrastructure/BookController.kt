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
    fun create(@RequestBody bookDTO: BookDTO): BookDTO {
        return bookService.save(bookDTO)
    }

    @DeleteMapping
    fun delete(@RequestBody requestBody: DeleteRequestBody): HttpStatus {
        bookService.deleteById(requestBody.id)
        return HttpStatus.OK
    }

    @GetMapping("/{id}")
    fun show(@PathVariable id: UUID): ResponseEntity<Any> {
        val bookDTO = bookService.showById(id)

        return if(bookDTO != null) {
            return ResponseEntity.ok(bookDTO)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not exists!")
        }
    }
}