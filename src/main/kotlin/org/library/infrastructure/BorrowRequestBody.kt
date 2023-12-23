package org.library.infrastructure

import java.util.*

data class BorrowRequestBody(
    val bookId: UUID,
    val userId: Long,
)