package tech.harrynull.galleryneo.persistence

import org.springframework.data.repository.CrudRepository

interface DbImageRepo : CrudRepository<DbImage?, Long?> {
    fun existsByStoreId(storeId: String): Boolean
    fun findAllByUploader_Name(name: String): List<DbImage>
}
