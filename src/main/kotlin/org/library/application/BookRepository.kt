package org.library.application

import org.library.entity.Book

interface BookRepository {
    fun save(book: Book): Boolean
}