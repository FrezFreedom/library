package org.library.entity

import jakarta.persistence.*

@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    var username: String? = null,

    @Column(nullable = true)
    var name: String? = null,

    @Column(nullable = false, unique = true)
    var email: String? = null,

    @Column(nullable = false)
    var password: String? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    var roles: Set<Role>? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    val books: MutableSet<Book> = mutableSetOf()
)