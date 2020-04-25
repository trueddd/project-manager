package auth

import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import service.users.UsersService
import utils.AppEnvironment

fun Authentication.Configuration.setupJwtAuth(usersService: UsersService) {
    jwt {
        verifier(JwtConfig.verifier)
        realm = AppEnvironment.JWT.realm()
        validate { credentials ->
            val id = credentials.payload.getClaim("id").asInt() ?: return@validate null
            return@validate usersService.getUserById(id).getSuccess()
        }
    }
}