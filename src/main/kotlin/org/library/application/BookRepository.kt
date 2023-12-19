package org.library.application

import org.library.entity.Book
import java.util.*

interface BookRepository{
    fun save(book: Book): Book
    fun deleteById(id: UUID)
}