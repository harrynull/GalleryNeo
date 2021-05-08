package tech.harrynull.galleryneo.persistence

import tech.harrynull.galleryneo.proto.User
import javax.persistence.*

@Entity(name = "user")
@Table(
    name = "users", indexes = [
    Index(name = "idx_name", columnList = "name", unique = false),
    Index(name = "idx_email", columnList = "email", unique = true),
]
)
data class DbUser(
    @Column
    var name: String,

    @Column
    var email: String,

    @Column
    var hashedPassword: String,

    @Column
    var sessionToken: String,

    @Id @GeneratedValue
    var id: Long? = null
) {
    fun toProto(): User {
        return User(
            name = name,
            email = email,
        )
    }
}
