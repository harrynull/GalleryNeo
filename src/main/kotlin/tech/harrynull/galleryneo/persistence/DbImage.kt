package tech.harrynull.galleryneo.persistence

import tech.harrynull.galleryneo.proto.Image
import tech.harrynull.galleryneo.proto.User
import javax.persistence.*

@Entity(name = "image")
@Table(
    name = "images", indexes = [
    Index(name = "idx_imageName", columnList = "imageName", unique = false),
    Index(name = "idx_uploader", columnList = "uploader_id", unique = true),
]
)
data class DbImage(
    @Column
    var description: String,

    @Column
    var imageName: String,

    @Column
    var timeUploadedMillis: Long,

    @OneToOne(fetch = FetchType.EAGER) @JoinColumn
    var uploader: DbUser,

    @Id @GeneratedValue
    var id: Long? = null
) {
    fun toProto(): Image {
        return Image(
            description = description,
            url = "/$imageName",
            uploaderName = uploader.name,
            timeUploadedMillis = timeUploadedMillis,
        )
    }
}
