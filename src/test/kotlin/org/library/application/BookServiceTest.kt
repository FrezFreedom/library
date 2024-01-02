package org.library.application

import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.library.entity.Book
import org.library.entity.User
import java.util.*


class BookServiceTest {

    @Test
    fun `should return book when save book invokes`() {
        val bookDTO = BookDTO(
            title = "The Little Prince",
            isbn = "978-3-16-148410-0"
        )
        val mockBookRepository = mockk<BookRepository>()
        val mockUserRepository = mockk<UserRepository>()
        val bookSlot = slot<Book>()
        every { mockBookRepository.save(capture(bookSlot)) } returns mockk()
        val bookService = BookService(mockBookRepository, mockUserRepository)

        bookService.save(bookDTO)

        assertEquals(bookDTO.title, bookSlot.captured.title)
        assertEquals(bookDTO.isbn, bookSlot.captured.isbn)
    }

    @Test
    fun `should return true when delete book invokes`() {
        val id: UUID = UUID.randomUUID()
        val mockBookRepository = mockk<BookRepository>()
        val mockUserRepository = mockk<UserRepository>()
        every { mockBookRepository.deleteById(id) } just runs
        every { mockBookRepository.showById(id) } returns mockk()
        val bookService = BookService(mockBookRepository, mockUserRepository)

        bookService.deleteById(id)

        verify { mockBookRepository.deleteById(id) }
    }

    @Test
    fun `should throw exception when delete book that not exist`() {
        val id: UUID = UUID.randomUUID()
        val mockBookRepository = mockk<BookRepository>()
        val mockUserRepository = mockk<UserRepository>()
        every { mockBookRepository.showById(id) } returns null
        val bookService = BookService(mockBookRepository, mockUserRepository)

        assertThrows(ElementNotFoundException::class.java) {
            bookService.deleteById(id)
        }
        verify { mockBookRepository.showById(id) }
    }

    @Test
    fun `should show book when show function invokes`() {
        val id: UUID = UUID.randomUUID()
        val mockBookRepository = mockk<BookRepository>()
        val mockUserRepository = mockk<UserRepository>()
        val book = Book(id, "example_title", "example_isbn")
        every { mockBookRepository.showById(id) } returns book
        val bookService = BookService(mockBookRepository, mockUserRepository)
        val expectedBookDTO = BookDTO("example_title", "example_isbn")

        val result = bookService.showById(id)

        assertEquals(expectedBookDTO.title, result.title)
        assertEquals(expectedBookDTO.isbn, result.isbn)
    }

    @Test
    fun `should return all books when findAll invokes`() {
        val mockBookRepository = mockk<BookRepository>()
        val mockUserRepository = mockk<UserRepository>()
        val books = listOf(Book(title = "x", isbn = "y"), Book(title = "xx", isbn = "yy"))
        val expectedBooks = books.map { BookDTO.createFromBook(it) }
        every { mockBookRepository.findAll() } returns books
        val bookService = BookService(mockBookRepository, mockUserRepository)

        val result = bookService.findAll()

        expectedBooks.forEachIndexed { index, expectedBook ->
            assertEquals(expectedBook.title, result[index].title)
            assertEquals(expectedBook.isbn, result[index].isbn)
        }
    }

    @Test
    fun `should borrow book to the user when borrow function invokes`(){
        val mockBookRepository = mockk<BookRepository>()
        val mockUserRepository = mockk<UserRepository>()
        val userId = 5L
        val bookId = UUID.randomUUID()
        val mockUser = User(username = "frez", name = "faraz", email = "frez@gmail.com", password = "####", id = userId)
        val mockBook = Book(bookId, "title", "isbn")
        every { mockBookRepository.showById(bookId) } returns mockBook
        every { mockUserRepository.findById(userId) } returns mockUser
        val bookSlot = slot<Book>()
        every { mockBookRepository.save(capture(bookSlot)) } returns mockk()
        val bookService = BookService(mockBookRepository, mockUserRepository)
        val expectedBook = Book(bookId, "title", "isbn", mockUser)

        bookService.borrowBook(bookId, userId)

        assertEquals(expectedBook, bookSlot.captured)
        verify { mockBookRepository.save(mockBook) }
        verify { mockBookRepository.showById(bookId) }
        verify { mockUserRepository.findById(userId) }
    }

    @Test
    fun `should throw NoSuchElementException when borrow function invokes with invalid book id and invalid user id`(){
        val mockBookRepository = mockk<BookRepository>()
        val mockUserRepository = mockk<UserRepository>()
        val userId = 5L
        val bookId = UUID.randomUUID()
        every { mockBookRepository.showById(bookId) } returns null
        every { mockUserRepository.findById(userId) } returns mockk()
        val bookService = BookService(mockBookRepository, mockUserRepository)

        assertThrows(ElementNotFoundException::class.java) {
            bookService.borrowBook(bookId, userId)
        }
        verify { mockBookRepository.showById(bookId) }
    }

    @Test
    fun `should throw NoSuchElementException when borrow function invokes with invalid user id and invalid user id`(){
        val mockBookRepository = mockk<BookRepository>()
        val mockUserRepository = mockk<UserRepository>()
        val userId = 5L
        val bookId = UUID.randomUUID()
        every { mockUserRepository.findById(userId) } returns null
        val bookService = BookService(mockBookRepository, mockUserRepository)

        assertThrows(ElementNotFoundException::class.java) {
            bookService.borrowBook(bookId, userId)
        }
        verify { mockUserRepository.findById(userId) }
    }

    @Test
    fun `should throw NoSuchElementException when borrow function invokes with invalid book id`(){
        val mockBookRepository = mockk<BookRepository>()
        val mockUserRepository = mockk<UserRepository>()
        val userId = 5L
        val bookId = UUID.randomUUID()
        every { mockBookRepository.showById(bookId) } returns null
        every { mockUserRepository.findById(userId) } returns null
        val bookService = BookService(mockBookRepository, mockUserRepository)

        assertThrows(ElementNotFoundException::class.java) {
            bookService.borrowBook(bookId, userId)
        }
        verify { mockUserRepository.findById(userId) }
    }
    @Test
    fun `should return book to library when return function invokes`(){
        val mockBookRepository = mockk<BookRepository>()
        val mockUserRepository = mockk<UserRepository>()
        val bookId = UUID.randomUUID()
        val book = Book(bookId, "title", "isbn", mockk())
        val savedBook = Book(bookId, "title", "isbn", null)
        every { mockBookRepository.showById(bookId) } returns book
        every { mockBookRepository.save(savedBook) } returns mockk()
        val bookService = BookService(mockBookRepository, mockUserRepository)

        bookService.returnBook(bookId)

        verify { mockBookRepository.showById(bookId) }
        verify { mockBookRepository.save(savedBook) }
    }

    @Test
    fun `should throw exception when return function invokes for invalid book id`(){
        val mockBookRepository = mockk<BookRepository>()
        val mockUserRepository = mockk<UserRepository>()
        val bookId = UUID.randomUUID()
        every { mockBookRepository.showById(bookId) } throws NoSuchElementException()
        val bookService = BookService(mockBookRepository, mockUserRepository)

        assertThrows(NoSuchElementException::class.java) {
            bookService.returnBook(bookId)
        }
        verify { mockBookRepository.showById(bookId) }
    }
}