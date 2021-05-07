package tech.harrynull.galleryneo.persistence

import javax.persistence.*

@Entity(name = "user")
@Table(
    name = "users", indexes = [
        Index(name = "idx_email", columnList = "email", unique = true),
    ]
)
data class DbUser(
    @Id @GeneratedValue
    var id: Long? = null
) {
    fun toProto(): Nothing = TODO()
}
