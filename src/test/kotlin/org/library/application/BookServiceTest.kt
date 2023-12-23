package org.library.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.library.entity.Book
import java.util.*


class BookServiceTest {

    @Test
    fun `should return book when save book invokes`(){
        val book = Book(title="The Little Prince",
                        isbn="978-3-16-148410-0")
        val bookDTO = BookDTO(title="The Little Prince",
                              isbn="978-3-16-148410-0")
        val mockBookRepository = mockk<BookRepository>()
        every { mockBookRepository.save(any()) } returns book
        val bookService = BookService(mockBookRepository)

        val result = bookService.save(bookDTO)

        Assertions.assertEquals(bookDTO, result)
    }

    @Test
    fun `should return true when delete book invokes`(){
        val id: UUID = UUID.randomUUID()
        val mockBookRepository = mockk<BookRepository>()
        every { mockBookRepository.deleteById(id) } returns Unit
        every { mockBookRepository.showById(id) } returns Book(title = "x", isbn = "z")

        val bookService = BookService(mockBookRepository)

        val result = bookService.deleteById(id)

        verify { mockBookRepository.deleteById(id) }
        Assertions.assertEquals(Unit, result)
    }

    @Test
    fun `should throw exception when delete book that not exist`(){
        val id: UUID = UUID.randomUUID()
        val mockBookRepository = mockk<BookRepository>()
        every { mockBookRepository.showById(id) } returns null

        val bookService = BookService(mockBookRepository)

        assertThrows(NoSuchElementException::class.java) {
            bookService.deleteById(id)
        }

        verify { mockBookRepository.showById(id) }
    }

    @Test
    fun `should show book when show function invokes`(){
        val id: UUID = UUID.randomUUID()
        val mockBookRepository = mockk<BookRepository>()
        val book = Book(id, "example_title", "example_isbn")
        every { mockBookRepository.showById(id) } returns book
        val bookService = BookService(mockBookRepository)
        val expectedBookDTO = BookDTO("example_title", "example_isbn")

        val result = bookService.showById(id)

        Assertions.assertEquals(expectedBookDTO, result)
    }

    @Test
    fun `should return all books when findAll invokes`(){
        val mockBookRepository = mockk<BookRepository>()
        val books = listOf(Book(title = "x", isbn = "y"), Book(title = "xx", isbn = "yy"))
        val expectedBooks = books.map { it.let { BookDTO(it.title, it.isbn) } }
        every { mockBookRepository.findAll() } returns books
        val bookService = BookService(mockBookRepository)

        val result = bookService.findAll()

        Assertions.assertEquals(expectedBooks, result)
    }
}