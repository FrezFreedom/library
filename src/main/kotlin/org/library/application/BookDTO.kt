package org.library.application

import org.library.entity.Book
import java.util.UUID

data class BookDTO(
    val title: String = "",
    val isbn: String = "",
    val id: UUID? = null,
) {
    companion object {
        fun createFromBook(book: Book): BookDTO {
            return book.let { BookDTO(it.title, it.isbn, it.id) }
        }
    }
}