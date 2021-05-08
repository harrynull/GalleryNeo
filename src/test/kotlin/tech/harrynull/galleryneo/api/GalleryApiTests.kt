package tech.harrynull.galleryneo.api

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import javax.transaction.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GalleryApiTests {
    @Autowired
    private lateinit var galleryApi: GalleryApi

    @Test
    fun `can upload, view and list images`() {

    }

    @Test
    fun `need to be logged in before uploading images`() {

    }

    @Test
    fun `image is deleted if nothing refers to it anymore`() {

    }

    @Test
    fun `private image is only accessible by its uploader`() {

    }

    @Test
    fun `private image is not in the list`() {

    }

    @Test
    fun `only the uploader can delete an image`() {

    }
}