package tech.harrynull.galleryneo.persistence

import org.springframework.data.repository.CrudRepository

interface DbUserRepo : CrudRepository<DbUser?, Long?> {
    fun findByName(email: String): DbUser?
    fun findBySessionToken(token: String): DbUser?
}