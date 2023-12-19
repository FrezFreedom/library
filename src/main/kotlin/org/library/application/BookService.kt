package org.library.application

import org.library.entity.Book
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BookService @Autowired constructor(
    private val bookRepository: BookRepository
) {

    fun save(bookDTO: BookDTO): BookDTO {
        val result = bookRepository.save(bookDTO.let { Book(title = it.title, isbn = it.isbn) })
        println(result.id)
        return result.let { BookDTO(it.title,it.isbn) }
    }

    fun deleteById(id: UUID){
        bookRepository.deleteById(id)
    }

    fun showById(id: UUID): BookDTO? {
        val book = bookRepository.showById(id)
        return book?.let { BookDTO(it.title, it.isbn) }
    }
}