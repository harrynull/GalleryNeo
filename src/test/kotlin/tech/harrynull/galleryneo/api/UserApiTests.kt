package tech.harrynull.galleryneo.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import tech.harrynull.galleryneo.proto.LoginRequest
import tech.harrynull.galleryneo.proto.RegisterRequest
import javax.transaction.Transactional

@Transactional
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserApiTests {
    @Autowired
    private lateinit var userApi: UserApi

    @Test
    fun `can register and login`() {
        val resp = MockHttpServletResponse()

        val result = userApi.register(
            RegisterRequest(
                email = "test@example.com",
                name = "Joe",
                password = "very_secure_password"
            ), resp
        )
        assertThat(result.isSuccessful).isEqualTo(true)

        with(result.user!!) {
            assertThat(name).isEqualTo("Joe")
            assertThat(email).isEqualTo("test@example.com")
        }
        assertThat(resp.cookies[0].name).isEqualTo("session")
        val sessionValue = resp.cookies[0].value
        assertThat(sessionValue).isNotEmpty()

        // now start a new session
        val resp2 = MockHttpServletResponse()

        assertThat(
            userApi.login(
                LoginRequest(
                    email = "test@example.com",
                    password = "very_secure_password_but_wrong"
                ), resp2
            ).failMessage
        ).isEqualTo("Username or password incorrect")
        assertThat(resp2.cookies.size).isEqualTo(0)

        val result2 = userApi.login(
            LoginRequest(
                email = "test@example.com",
                password = "very_secure_password"
            ), resp2
        )
        assertThat(result2.isSuccessful).isTrue()
        with(result2.user!!) {
            assertThat(name).isEqualTo("Joe")
            assertThat(email).isEqualTo("test@example.com")
        }
        val newSessionId = resp2.cookies.single { it.name == "session" }.value
        assertThat(newSessionId).isNotEmpty()
        assertThat(newSessionId).isNotEqualTo(sessionValue)
    }

    @Test
    fun `cannot login into non-existent user`() {
        assertThat(
            userApi.register(
                RegisterRequest(
                    email = "test@example.com",
                    name = "Joe",
                    password = "very_secure_password"
                ), MockHttpServletResponse()
            ).failMessage
        ).isNull()

        assertThat(
            userApi.register(
                RegisterRequest(
                    email = "test@example.com",
                    name = "Joe",
                    password = "very_secure_password"
                ), MockHttpServletResponse()
            ).failMessage
        ).isEqualTo("Email address already exists")
    }

    @Test
    fun `cannot create two accounts with the same email`() {
        val resp = MockHttpServletResponse()
        assertThat(
            userApi.login(
                LoginRequest(
                    email = "random@example.com",
                    password = "very_secure_password"
                ), resp
            ).isSuccessful
        ).isFalse() // email not exist
        assertThat(resp.cookies).isEmpty()
    }

    @Test
    fun `registering needs all mandatory fields`() {
        // Send an empty request - will fail beacuse of missing mandatory fields
        with(userApi.register(RegisterRequest(), MockHttpServletResponse())) {
            assertThat(isSuccessful).isFalse()
            assertThat(user).isNull()
            assertThat(failMessage).isEqualTo("Email not provided")
        }

        with(userApi.register(RegisterRequest(email = "bad_email"), MockHttpServletResponse())) {
            assertThat(isSuccessful).isFalse()
            assertThat(user).isNull()
            assertThat(failMessage).isEqualTo("Email address not valid")
        }

        with(userApi.register(RegisterRequest(email = "ok@ex.com"), MockHttpServletResponse())) {
            assertThat(isSuccessful).isFalse()
            assertThat(user).isNull()
            assertThat(failMessage).isEqualTo("Password not provided")
        }

        with(userApi.register(RegisterRequest(email = "ok@ex.com", password = "Joe"), MockHttpServletResponse())) {
            assertThat(isSuccessful).isFalse()
            assertThat(user).isNull()
            assertThat(failMessage).isEqualTo("Name not provided")
        }
    }
}