package org.library.infrastructure

import org.library.application.CrudUserDto
import org.library.application.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@RestControllerAdvice
class UserController @Autowired constructor(private val userService: UserService)
{
    @PostMapping
    fun create(@RequestBody crudUserDto: CrudUserDto) =
        userService.save(crudUserDto)


    @GetMapping("/{id}")
    fun show(@PathVariable id: Long) =
        userService.showById(id)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) =
        userService.deleteById(id)

    @GetMapping
    fun showAll() =
        userService.findAll()

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody crudUserDto: CrudUserDto) =
        userService.update(id, crudUserDto)
}