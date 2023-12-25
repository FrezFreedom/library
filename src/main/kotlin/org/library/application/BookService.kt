package org.library.application

import org.library.entity.Book
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class BookService @Autowired constructor(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) {

    fun save(bookDTO: BookDTO) {
        val temp = bookDTO.let { Book(title = it.title, isbn = it.isbn) }
        bookRepository.save(temp)
    }

    fun deleteById(id: UUID) {
        bookRepository.showById(id) ?: throw ElementNotFoundException("Book with ID $id not found")

        bookRepository.deleteById(id)
    }

    fun showById(id: UUID): BookDTO {
        val book = bookRepository.showById(id) ?: throw ElementNotFoundException("Book with ID $id not found")
        return BookDTO.createFromBook(book)
    }

    fun findAll(): List<BookDTO> {
        val books = bookRepository.findAll()
        return books.map { BookDTO.createFromBook(it) }
    }

    fun borrowBook(bookId: UUID, userId: Long) {
        val user = userRepository.findById(userId) ?: throw ElementNotFoundException("User with ID $userId not found")
        val book = bookRepository.showById(bookId) ?: throw ElementNotFoundException("Book with ID $bookId not found")

        if (!book.isBorrowed) {
            book.user = user
            bookRepository.save(book)
        } else {
            throw BookNotAvailableException("Book with ID $bookId is not available now!")
        }
    }

    fun returnBook(bookId: UUID) {
        val book = bookRepository.showById(bookId) ?: throw ElementNotFoundException("Book with ID $bookId not found")
        book.user = null
        bookRepository.save(book)
    }
}