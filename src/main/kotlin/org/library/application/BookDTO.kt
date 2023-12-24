package org.library.application

import java.util.UUID

data class BookDTO (
    val title: String,
    val isbn: String,
    val id: UUID? = null,
){
    constructor() : this("", "")
}