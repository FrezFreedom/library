package org.library.application

import org.library.entity.Book
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class BookService @Autowired constructor(@Qualifier("crud") private val bookRepository: BookRepository) {

    fun save(book: Book): Book {
        return bookRepository.save(book)
    }
}