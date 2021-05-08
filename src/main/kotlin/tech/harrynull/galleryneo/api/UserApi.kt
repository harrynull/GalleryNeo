package tech.harrynull.galleryneo.api

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import tech.harrynull.galleryneo.persistence.DbUser
import tech.harrynull.galleryneo.persistence.DbUserRepo
import tech.harrynull.galleryneo.proto.LoginOrRegisterResponse
import tech.harrynull.galleryneo.proto.LoginRequest
import tech.harrynull.galleryneo.proto.RegisterRequest
import tech.harrynull.galleryneo.utils.PasswordHasher
import tech.harrynull.galleryneo.utils.SessionManager
import javax.servlet.http.HttpServletResponse

@RestController
class UserApi(
    val dbUserRepo: DbUserRepo,
    val passwordHasher: PasswordHasher,
    val sessionManager: SessionManager,
){
    @PostMapping("/user/login")
    fun login(@RequestBody loginRequest: LoginRequest, response: HttpServletResponse): LoginOrRegisterResponse {
        val email = loginRequest.email?.takeIf { it.isNotEmpty() }
            ?: return failResponse("Email not provided")

        val password = loginRequest.password?.takeIf { it.isNotEmpty() }
            ?: return failResponse("Password not provided")

        val dbUser = dbUserRepo.findByEmail(email)

        if (dbUser?.hashedPassword != null && passwordHasher.check(dbUser.hashedPassword, password)) {
            sessionManager.generateAndSendToken(response, dbUser)
            dbUserRepo.save(dbUser)
            return LoginOrRegisterResponse(isSuccessful = true, user = dbUser.toProto())
        }

        return failResponse("Username or password incorrect")
    }

    @PostMapping("/user/register")
    fun register(
        @RequestBody registerRequest: RegisterRequest,
        response: HttpServletResponse
    ): LoginOrRegisterResponse {
        val email = registerRequest.email?.takeIf { it.isNotEmpty() }
            ?: return failResponse("Email not provided")

        if (!email.contains("@")) {
            return failResponse("Email address not valid")
        }

        val password = registerRequest.password?.takeIf { it.isNotEmpty() }
            ?: return failResponse("Password not provided")

        val name = registerRequest.name?.takeIf { it.isNotEmpty() }
            ?: return failResponse("Name not provided")

        if (dbUserRepo.findByEmail(email) != null) {
            return failResponse("Email address already exists")
        }

        val dbUser = DbUser(
            email = email,
            name = name,
            hashedPassword = passwordHasher.hash(password),
            sessionToken = "", // will be assigned later
        )

        sessionManager.generateAndSendToken(response, dbUser)
        dbUserRepo.save(dbUser)

        return LoginOrRegisterResponse(isSuccessful = true, user = dbUser.toProto())
    }

    private fun failResponse(reason: String): LoginOrRegisterResponse {
        return LoginOrRegisterResponse(isSuccessful = false, failMessage = reason)
    }
}