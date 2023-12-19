package org.library.infrastructure

import org.library.application.BookRepository
import org.library.entity.Book
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface HibernateBookRepository : BookRepository, CrudRepository<Book, UUID> {

    @Query("SELECT b FROM Book b WHERE b.id = :id")
    override fun showById(id: UUID): Book?

//    override fun showById(id: UUID): Book {
//        val book = findById(id)
//
//        return book.get()
//    }
}