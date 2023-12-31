package org.library.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
data class Book(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val isbn: String,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    var user: User? = null
) {
    val isBorrowed: Boolean get() = user != null
}