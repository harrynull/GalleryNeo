package tech.harrynull.galleryneo.api

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import javax.transaction.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class GalleryApiTests {

}