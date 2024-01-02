package org.library.infrastructure

import org.library.application.RoleRepository
import org.library.entity.Role
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface HibernateRoleRepository: RoleRepository, CrudRepository<Role, Int>