package org.library.application

import org.library.entity.Book
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BookService @Autowired constructor(
    private val bookRepository: BookRepository
) {

    fun save(bookDTO: BookDTO) {
        val temp = bookDTO.let { Book(title = it.title, isbn = it.isbn) }
        bookRepository.save(temp)
    }

    fun deleteById(id: UUID) {
        if (bookRepository.showById(id) == null) {
            throw NoSuchElementException()
        }
        bookRepository.deleteById(id)
    }

    fun showById(id: UUID): BookDTO? {
        val book = bookRepository.showById(id)
        return book?.let { BookDTO(it.title, it.isbn, it.id) }
    }

    fun findAll(): List<BookDTO> {
        val books = bookRepository.findAll()
        return books.map { BookDTO(it.title, it.isbn, it.id) }
    }
}