package org.library.infrastructure

import org.library.application.BookNotAvailableException
import org.library.application.ElementNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ElementNotFoundException::class)
    fun handleNotFound(exception: ElementNotFoundException): ResponseEntity<String> =
        ResponseEntity(exception.message, HttpStatus.NOT_FOUND)

    @ExceptionHandler(BookNotAvailableException::class)
    fun handleBookNotAvailable(exception: Exception): ResponseEntity<String> =
        ResponseEntity(exception.message, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(Exception::class)
    fun handleOtherExceptions(exception: Exception): ResponseEntity<String> =
        ResponseEntity("Server error occurred", HttpStatus.INTERNAL_SERVER_ERROR)
}