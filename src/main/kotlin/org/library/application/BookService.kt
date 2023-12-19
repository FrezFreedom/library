package org.library.application

import org.library.entity.Book

class BookService(private val bookRepository: BookRepository) {

    fun save(book: Book): Boolean {
        return bookRepository.save(book)
    }
}