package org.library.application

import org.library.entity.Role

interface RoleRepository {
    fun save(role: Role): Role
}