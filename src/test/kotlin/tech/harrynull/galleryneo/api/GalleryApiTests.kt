package tech.harrynull.galleryneo.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import tech.harrynull.galleryneo.proto.Image
import tech.harrynull.galleryneo.utils.ImageStore
import tech.harrynull.galleryneo.utils.UserSession
import tech.harrynull.galleryneo.utils.UserSessionFactory
import javax.transaction.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GalleryApiTests {
    @Autowired
    private lateinit var userSessionFactory: UserSessionFactory

    @Autowired
    private lateinit var imageStore: ImageStore

    private lateinit var user1: UserSession
    private lateinit var user2: UserSession
    private lateinit var guest: UserSession  // not logged in

    @BeforeEach
    fun setup() {
        user1 = userSessionFactory.createUser("user1")
        user2 = userSessionFactory.createUser("user2")
        guest = userSessionFactory.createGuestUser()
    }

    @Test
    fun `can upload, view and list images`() {
        val result = user1.uploadImage("test".toByteArray(), "a test image")
        with(result) {
            assertThat(isSuccessful).isEqualTo(true)
            assertThat(image!!.description).isEqualTo("a test image")
            assertThat(image!!.uploaderName).isEqualTo("user1")
            assertThat(image!!.storeId).isNotBlank()
        }
        val storeId = result.image!!.storeId!! // SHA 256
        val id = result.image!!.id!!

        // try view the image with its id and storeId
        assertThat(guest.getImage(id.toString()).body).isEqualTo("test".toByteArray())
        assertThat(guest.getImage(storeId).body).isEqualTo("test".toByteArray())

        with(guest.getImageMetaInfo(id)!!) {
            assertThat(description).isEqualTo("a test image")
            assertThat(uploaderName).isEqualTo("user1")
            assertThat(storeId).isEqualTo(storeId)
            assertThat(id).isEqualTo(id)
        }

        assertThat(guest.listImages().images.map { it.id }).containsExactly(id)

        // Now another user upload the same image again with no description
        val result2 = user2.uploadImage("test".toByteArray(), null)
        with(result2) {
            assertThat(isSuccessful).isEqualTo(true)
            assertThat(image!!.description).isEqualTo("")
            assertThat(image!!.uploaderName).isEqualTo("user2")
            assertThat(image!!.storeId).isEqualTo(storeId)
            assertThat(image!!.id).isNotEqualTo(id)
        }
        val newId = result2.image!!.id!!

        // try view the image with its id and storeId
        assertThat(guest.getImage(newId.toString()).body).isEqualTo("test".toByteArray())
        assertThat(guest.getImage(storeId).body).isEqualTo("test".toByteArray())

        assertThat(guest.listImages().images.map { it.id }).containsExactlyInAnyOrder(id, newId)
    }

    @Test
    fun `non-existent image return 404`() {
        assertThat(guest.getImage("1").statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun `need to be logged in before uploading images`() {
        assertThat(guest.uploadImage("test".toByteArray(), "desc").isSuccessful).isFalse()
    }

    @Test
    fun `image is deleted if nothing refers to it anymore`() {
        val content = "test".toByteArray()
        val image1 = user1.uploadImage(content, "a test image").image!!
        val image2 = user1.uploadImage(content, "a test image 2").image!! // upload again
        assertThat(image1.storeId).isEqualTo(image2.storeId)

        val image1Id = image1.id!!
        val image2Id = image2.id!!
        val storeId = image1.storeId!!

        user1.deleteImage(image1Id)
        // only image 1 is not accessible
        assertThat(guest.getImage(image1Id.toString()).statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(guest.getImage(image2Id.toString()).body!!).isEqualTo(content)

        // now that all the images are deleted, the actual image stored is also deleted
        user1.deleteImage(image2Id)
        assertThat(guest.getImage(image1Id.toString()).statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(guest.getImage(image2Id.toString()).statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        assertThat(imageStore.readImage(storeId)).isNull()
    }

    @Test
    fun `private image is only accessible by its uploader and anyone with direct url`() {
        val image = user1.uploadImage("test".toByteArray(), "a test image", Image.Permission.HIDDEN).image!!
        assertThat(user1.getImage(image.id!!.toString()).body).isEqualTo("test".toByteArray())
        assertThat(user2.getImage(image.id!!.toString()).body).isNullOrEmpty()
        // You can access the image if you know the SHA256. So they can link the image somewhere else.
        assertThat(user2.getImage(image.storeId!!).body).isEqualTo("test".toByteArray())
        assertThat(user2.getImageMetaInfo(image.id!!)).isNull()
        assertThat(guest.getImage(image.id!!.toString()).body).isNullOrEmpty()
        assertThat(guest.getImage(image.storeId!!).body).isEqualTo("test".toByteArray())
        assertThat(guest.getImageMetaInfo(image.id!!)).isNull()
    }

    @Test
    fun `private image is not in the list`() {
        user1.uploadImage("test".toByteArray(), "a test image", Image.Permission.HIDDEN).image!!
        assertThat(user1.listImages().images).isEmpty()
    }

    @Test
    fun `only the uploader can delete an image`() {
        val image = user1.uploadImage("test".toByteArray(), "a test image").image!!
        // user 2 and guest can't remove user 1's image
        assertThat(user2.deleteImage(image.id!!).statusCode).isEqualTo(HttpStatus.FORBIDDEN)
        assertThat(guest.deleteImage(image.id!!).statusCode).isEqualTo(HttpStatus.FORBIDDEN)
        // image is still accessible
        assertThat(guest.getImage(image.id!!.toString()).body!!).isEqualTo("test".toByteArray())
    }

    @Test
    fun `view uploaded images by someone`() {
        val imagePublic = user1.uploadImage("test".toByteArray(), null, Image.Permission.PUBLIC).image!!
        val imageHidden = user1.uploadImage("test".toByteArray(), null, Image.Permission.HIDDEN).image!!
        // images uploaded by user 2 will not be shown in the user 1's uploaded image list
        user2.uploadImage("test".toByteArray(), null, Image.Permission.PUBLIC).image!!

        assertThat(user1.getAllImagesUploadedBy("user1").images.map { it.id })
            .containsExactlyInAnyOrder(imagePublic.id, imageHidden.id)

        // if user 2/guest views user 1's uploaded images, they can only see the public ones.
        assertThat(user2.getAllImagesUploadedBy("user1").images.map { it.id })
            .containsExactlyInAnyOrder(imagePublic.id)
        assertThat(guest.getAllImagesUploadedBy("user1").images.map { it.id })
            .containsExactlyInAnyOrder(imagePublic.id)
    }
}