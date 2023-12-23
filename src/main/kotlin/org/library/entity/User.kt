package org.library.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    val books: MutableSet<Book> = mutableSetOf()
)