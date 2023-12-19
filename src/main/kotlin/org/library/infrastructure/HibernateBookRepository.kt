package org.library.infrastructure

import org.library.application.BookRepository
import org.library.entity.Book
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import java.util.*

@Component("crud")
interface HibernateBookRepository : BookRepository, CrudRepository<Book, UUID>