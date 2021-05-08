package tech.harrynull.galleryneo.persistence

import org.springframework.data.repository.CrudRepository

interface DbImageRepo : CrudRepository<DbImage?, Long?>
