package tech.harrynull.galleryneo.api

import org.springframework.web.bind.annotation.GetMapping
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
    private val dbUserRepo: DbUserRepo,
    private val passwordHasher: PasswordHasher,
    private val sessionManager: SessionManager,
){
    @PostMapping("/user/login")
    fun login(@RequestBody loginRequest: LoginRequest, response: HttpServletResponse): LoginOrRegisterResponse {
        val name = loginRequest.name?.takeIf { it.isNotEmpty() }
            ?: return failResponse("Name not provided")

        val password = loginRequest.password?.takeIf { it.isNotEmpty() }
            ?: return failResponse("Password not provided")

        val dbUser = dbUserRepo.findByName(name)

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
        val name = registerRequest.name?.takeIf { it.isNotEmpty() }
            ?: return failResponse("Name not provided")

        val password = registerRequest.password?.takeIf { it.isNotEmpty() }
            ?: return failResponse("Password not provided")

        if (dbUserRepo.findByName(name) != null) {
            return failResponse("Name is already taken")
        }

        val dbUser = DbUser(
            name = name,
            hashedPassword = passwordHasher.hash(password),
            sessionToken = "", // will be assigned later
        )

        sessionManager.generateAndSendToken(response, dbUser)
        dbUserRepo.save(dbUser)

        return LoginOrRegisterResponse(isSuccessful = true, user = dbUser.toProto())
    }

    @GetMapping("/user/{name}/uploaded")
    fun uploadedImages() {

    }

    private fun failResponse(reason: String): LoginOrRegisterResponse {
        return LoginOrRegisterResponse(isSuccessful = false, failMessage = reason)
    }
}