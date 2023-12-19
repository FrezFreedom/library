package org.library.application

import org.library.entity.Book
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.util.*

interface BookRepository{
    fun save(book: Book): Book
}