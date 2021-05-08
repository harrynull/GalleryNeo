package tech.harrynull.galleryneo.utils

import java.lang.RuntimeException

class ImageNotFoundException: RuntimeException()

interface ImageStore {
    // return image name - hash of the image
    fun storeImage(content: ByteArray): String

    // get the content of the image from its id, return null if path is illegal or file does not exist
    fun readImage(storeId: String): ByteArray?

    // delete the image. ImageNotFoundException will be thrown if file not found or illegal
    fun deleteImage(storeId: String)
}
