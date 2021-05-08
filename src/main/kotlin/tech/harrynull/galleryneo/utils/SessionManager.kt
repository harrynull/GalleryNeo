package tech.harrynull.galleryneo.utils

import net.bytebuddy.utility.RandomString
import org.springframework.stereotype.Component
import tech.harrynull.galleryneo.persistence.DbUser
import tech.harrynull.galleryneo.persistence.DbUserRepo
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

private const val TOKEN_LENGTH = 20
private const val SESSION_COOKIE_NAME = "session"

@Component
class SessionManager(private val dbUserRepo: DbUserRepo) {
    private val randomString = RandomString(TOKEN_LENGTH)

    fun generateToken(): String = randomString.nextString()

    fun generateAndSendToken(response: HttpServletResponse, dbUser: DbUser) {
        dbUser.sessionToken = generateToken()
        val cookie = Cookie(SESSION_COOKIE_NAME, dbUser.sessionToken).apply {
            isHttpOnly = true
            path = "/"
            secure = true
            maxAge = 60 * 60 * 24 * 30 // 30 days
        }
        response.addCookie(cookie)
    }

    fun getSessionToken(request: HttpServletRequest): String? {
        return request.cookies?.findLast { it.name == SESSION_COOKIE_NAME }?.value
    }

    fun getCurrentUser(request: HttpServletRequest): DbUser? {
        return getSessionToken(request)?.let { dbUserRepo.findBySessionToken(it) }
    }
}
