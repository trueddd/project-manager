package route

import db.data.UserCreateBody
import db.data.UserLoginRequest
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import org.koin.ktor.ext.inject
import service.login.LoginService
import utils.Endpoints
import utils.receiveSafe

fun Routing.loginRoutes() {

    val loginService by inject<LoginService>()

    post(Endpoints.Login.Register) {
        val userLogin = call.receiveSafe<UserCreateBody>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val loginResponse = loginService.register(userLogin)
        if (loginResponse != null) {
            call.respond(HttpStatusCode.OK, loginResponse)
        } else {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    post(Endpoints.Login.Refresh) {
        val userData = call.receiveSafe<UserLoginRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        val refreshResponse = loginService.login(userData)
        if (refreshResponse != null) {
            call.respond(HttpStatusCode.OK, refreshResponse)
        } else {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}