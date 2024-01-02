package org.library.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    var username: String,

    @Column(nullable = true)
    var name: String? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var password: String,


    @ManyToMany(fetch = FetchType.EAGER)
    var roles: Set<Role>? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    val books: MutableSet<Book> = mutableSetOf()
)