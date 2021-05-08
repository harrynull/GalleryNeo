package tech.harrynull.galleryneo.utils

import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.mock.web.MockMultipartFile
import org.springframework.stereotype.Component
import tech.harrynull.galleryneo.api.GalleryApi
import tech.harrynull.galleryneo.api.UserApi
import tech.harrynull.galleryneo.proto.RegisterRequest
import tech.harrynull.galleryneo.proto.UploadResult
import javax.servlet.http.Cookie

class UserSession(private val galleryApi: GalleryApi, private val cookies: Array<Cookie>) {
    private val request: MockHttpServletRequest
        get() {
            val req = MockHttpServletRequest()
            req.setCookies(*cookies)
            return req
        }

    fun uploadImage(content: ByteArray, description: String?): UploadResult {
        val image = MockMultipartFile("image", "image.jpg", MediaType.IMAGE_JPEG_VALUE, content)
        return galleryApi.uploadImage(request, image, description)
    }

    fun getImage(id: String) = galleryApi.getImage(id)

    fun getImageMetaInfo(id: Long) = galleryApi.getImageMetaInfo(id)

    fun listImages() = galleryApi.listImages()

    fun deleteImage(id: Long) = galleryApi.deleteImage(request, id)
}

@Component
class UserSessionFactory(private val galleryApi: GalleryApi, private val userApi: UserApi) {
    fun createUser(name: String, password: String = "default"): UserSession {
        val resp = MockHttpServletResponse()
        userApi.register(RegisterRequest(name, password), resp)
        return UserSession(galleryApi, resp.cookies)
    }

    fun createGuestUser(): UserSession = UserSession(galleryApi, emptyArray())
}