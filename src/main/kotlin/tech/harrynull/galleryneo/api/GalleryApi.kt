package tech.harrynull.galleryneo.api

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import tech.harrynull.galleryneo.persistence.DbImage
import tech.harrynull.galleryneo.persistence.DbImageRepo
import tech.harrynull.galleryneo.proto.Image
import tech.harrynull.galleryneo.proto.ListOfImages
import tech.harrynull.galleryneo.proto.UploadResult
import tech.harrynull.galleryneo.utils.ImageStore
import tech.harrynull.galleryneo.utils.SessionManager
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class GalleryApi(
    private val dbImageRepo: DbImageRepo,
    private val sessionManager: SessionManager,
    private val imageStore: ImageStore,
) {
    @PostMapping("/images/upload")
    fun uploadImage(
        request: HttpServletRequest,
        @RequestParam("image") image: MultipartFile,
        @RequestParam("description") description: String?,
    ): UploadResult {
        val user = sessionManager.getCurrentUser(request)
            ?: return UploadResult(
                isSuccessful = false,
                failMessage = "You need to be logged in before uploading images"
            )

        val dbImage = dbImageRepo.save(DbImage.create(
            content = image.bytes,
            description = description,
            uploader = user,
            imageStore = imageStore,
        ))
        return UploadResult(isSuccessful = true, image = dbImage.toProto())
    }

    @GetMapping("images/meta/{id}")
    fun getImageMetaInfo(@PathVariable id: Long): Image? {
        // TODO: check permission
        return dbImageRepo.findByIdOrNull(id)?.toProto()
    }

    // it can use both store id (SHA 256) or image id (primary key)
    @GetMapping("images/{id}", produces = [
        MediaType.IMAGE_JPEG_VALUE,
        MediaType.IMAGE_GIF_VALUE,
        MediaType.IMAGE_PNG_VALUE
    ])
    fun getImage(@PathVariable id: String, response: HttpServletResponse): ResponseEntity<ByteArray> {
        // try to interpret it as SHA 256 (store id) first.
        val image = imageStore.readImage(id)
        if (image != null) return ResponseEntity.ok(image)

        val imageId = id.toLongOrNull() ?: return ResponseEntity.notFound().build()

        val dbImage = dbImageRepo.findByIdOrNull(imageId) ?: return ResponseEntity.notFound().build()
        // TODO: check permission
        return imageStore.readImage(dbImage.storeId)?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("images/{id}")
    fun deleteImage(request: HttpServletRequest, @PathVariable id: Long): ResponseEntity<Boolean> {
        val permissionDenied = ResponseEntity.status(HttpStatus.FORBIDDEN).build<Boolean>()
        val notFound = ResponseEntity.notFound().build<Boolean>()

        val dbUser = sessionManager.getCurrentUser(request) ?: return permissionDenied
        val dbImage = dbImageRepo.findByIdOrNull(id) ?: return notFound
        // TODO: check permission of image
        if (dbImage.uploader.id != dbUser.id) return permissionDenied

        val storeId = dbImage.storeId
        dbImageRepo.delete(dbImage) // hard deletion. GDPR is happy

        // free some disk space if no one else is referring to this image
        // can be further optimized by maintaining a ref counter.
        if (!dbImageRepo.existsByStoreId(storeId)) {
            imageStore.deleteImage(dbImage.storeId)
        }
        return ResponseEntity.ok(true)
    }

    @GetMapping("images/list")
    fun listImages(): ListOfImages {
        // TODO: check permission
        return ListOfImages(images = dbImageRepo.findAll().map { it!!.toProto() })
    }
}
