package tech.harrynull.galleryneo.utils

import org.bouncycastle.util.encoders.Hex
import org.springframework.stereotype.Component
import java.io.FileInputStream
import java.nio.file.Paths
import java.security.MessageDigest

private val idRegexPattern = "[A-Fa-f0-9]{64}".toRegex()

@Component
class LocalImageStore : ImageStore {
    // Well this should have been in a config file/env.
    // But really, in prod, it should be a CDN/file storage service (e.g. Amazon S3)
    private val imageStorePath = Paths.get("images")

    // return image name - hash of the image
    override fun storeImage(content: ByteArray): String {
        // digest is not thread-safe so create a new instance every time
        // didn't use ThreadLocal because it is not slow to create one.
        val digest = MessageDigest.getInstance("SHA-256")
        // stored name is the hash so that it is not guessable and also avoid wasting space
        // saving the same image.
        val hash = Hex.toHexString(digest.digest(content))
        /* TODO: ideally we want to check the header of the image file before actually store it
         * So users won't be able to abuse it as generic cloud drive.
         * However, uploading arbitrary file is safe since the path/name can't be controlled and is not
         * executable
         */
        val file = imageStorePath.resolve(hash).toFile()
        // We assume that two images with the same SHA256 have the same content.
        // Therefore, if the image exists, there is no need to save it again.
        if (!file.exists()) {
            file.outputStream().use { it.write(content) }
        }
        return hash
    }

    override fun readImage(storeId: String): ByteArray? {
        if (idRegexPattern.matchEntire(storeId) == null) return null

        return kotlin.runCatching {
            FileInputStream(imageStorePath.resolve(storeId).toFile()).use { it.readBytes() }
        }.getOrNull()
    }

    override fun deleteImage(storeId: String) {
        if (idRegexPattern.matchEntire(storeId) == null) throw ImageNotFoundException()

        val file = imageStorePath.resolve(storeId).toFile()
        if (!file.exists()) throw ImageNotFoundException()

        file.delete()
    }
}