package tech.harrynull.galleryneo.utils

interface ImageStore {
    // return image name - hash of the image
    fun storeImage(content: ByteArray): String

    // get the content of the image from its id, return null if path is illegal or file does not exist
    fun readImage(id: String): ByteArray?
}
