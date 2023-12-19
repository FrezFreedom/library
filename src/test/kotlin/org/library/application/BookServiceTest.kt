package org.library.application

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.library.entity.Book

class BookServiceTest {

    @Test
    fun `should return book when save book invokes`(){
        val book = Book(title="The Little Prince",
            isbn="978-3-16-148410-0")
        val mockBookRepository = mockk<BookRepository>()
        every { mockBookRepository.save(any()) } returns book
        val bookService = BookService(mockBookRepository)

        val result = bookService.save(book)

        Assertions.assertEquals(book, result)
    }
}