package org.library.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name="books")
data class Book(
    @Id
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val isbn: String,
)