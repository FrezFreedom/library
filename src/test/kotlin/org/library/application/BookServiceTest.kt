package org.library.application

import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.library.entity.Book
import java.util.*


class BookServiceTest {

    @Test
    fun `should return book when save book invokes`() {
        val bookDTO = BookDTO(
            title = "The Little Prince",
            isbn = "978-3-16-148410-0"
        )
        val mockBookRepository = mockk<BookRepository>()
        val bookSlot = slot<Book>()
        every { mockBookRepository.save(capture(bookSlot)) } returns mockk()
        val bookService = BookService(mockBookRepository)

        bookService.save(bookDTO)

        assertEquals(bookDTO.title, bookSlot.captured.title)
        assertEquals(bookDTO.isbn, bookSlot.captured.isbn)
    }

    @Test
    fun `should return true when delete book invokes`() {
        val id: UUID = UUID.randomUUID()
        val mockBookRepository = mockk<BookRepository>()
        every { mockBookRepository.deleteById(id) } just runs
        every { mockBookRepository.showById(id) } returns mockk()
        val bookService = BookService(mockBookRepository)

        bookService.deleteById(id)

        verify { mockBookRepository.deleteById(id) }
    }

    @Test
    fun `should throw exception when delete book that not exist`() {
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
    fun `should show book when show function invokes`() {
        val id: UUID = UUID.randomUUID()
        val mockBookRepository = mockk<BookRepository>()
        val book = Book(id, "example_title", "example_isbn")
        every { mockBookRepository.showById(id) } returns book
        val bookService = BookService(mockBookRepository)
        val expectedBookDTO = BookDTO("example_title", "example_isbn")

        val result = bookService.showById(id)

        assertEquals(expectedBookDTO.title, result?.title)
        assertEquals(expectedBookDTO.isbn, result?.isbn)
    }

    @Test
    fun `should return all books when findAll invokes`() {
        val mockBookRepository = mockk<BookRepository>()
        val books = listOf(Book(title = "x", isbn = "y"), Book(title = "xx", isbn = "yy"))
        val expectedBooks = books.map { BookDTO(it.title, it.isbn) }
        every { mockBookRepository.findAll() } returns books
        val bookService = BookService(mockBookRepository)

        val result = bookService.findAll()

        expectedBooks.forEachIndexed { index, expectedBook ->
            assertEquals(expectedBook.title, result[index].title)
            assertEquals(expectedBook.isbn, result[index].isbn)
        }

    }
}