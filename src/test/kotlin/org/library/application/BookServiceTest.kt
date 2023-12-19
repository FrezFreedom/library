package org.library.application

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.library.entity.Book

class BookServiceTest {

    @Test
    fun `should return true when save book invokes`(){
        val mockBookRepository = mockk<BookRepository>()
        every { mockBookRepository.save(any()) } returns true
        val bookService = BookService(mockBookRepository)
        val book = Book(title="The Little Prince",
                        isbn="978-3-16-148410-0")

        val result = bookService.save(book)

        Assertions.assertTrue(result)
    }
}