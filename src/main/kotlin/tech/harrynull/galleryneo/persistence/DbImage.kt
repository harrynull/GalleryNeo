package tech.harrynull.galleryneo.persistence

import tech.harrynull.galleryneo.proto.Image
import tech.harrynull.galleryneo.utils.ImageStore
import javax.persistence.*

@Entity(name = "image")
@Table(
    name = "images", indexes = [
    Index(name = "idx_storeId", columnList = "storeId"),
    Index(name = "idx_uploader", columnList = "uploader_id"),
]
)
data class DbImage(
    @Column
    var description: String,

    @Column
    var storeId: String, // hash of the image

    @Column
    var timeUploadedMillis: Long,

    @OneToOne(fetch = FetchType.EAGER) @JoinColumn
    var uploader: DbUser,

    @Column
    var permission: Image.Permission,

    @Id @GeneratedValue
    var id: Long? = null
) {
    fun toProto(): Image {
        return Image(
            description = description,
            storeId = storeId,
            id = id,
            uploaderName = uploader.name,
            timeUploadedMillis = timeUploadedMillis,
            permission = permission,
        )
    }

    companion object {
        // the caller is responsible for store the db object
        fun create(
            content: ByteArray,
            description: String?,
            uploader: DbUser,
            imageStore: ImageStore,
            permission: Image.Permission,
        ): DbImage {
            return DbImage(
                uploader = uploader,
                description = description ?: "",
                storeId = imageStore.storeImage(content),
                timeUploadedMillis = System.currentTimeMillis(),
                permission = permission,
            )
        }
    }
}
