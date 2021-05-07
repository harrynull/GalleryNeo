package tech.harrynull.galleryneo.persistence

import org.springframework.data.repository.CrudRepository

interface DbUserRepo : CrudRepository<DbUser?, Long?> {
    fun findByEmail(email: String): DbUser?
}