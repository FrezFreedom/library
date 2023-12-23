package org.library.infrastructure

import org.library.application.UserRepository
import org.library.entity.User
import org.springframework.data.repository.CrudRepository

interface HibernateUserRepository : UserRepository, CrudRepository<User, Long>