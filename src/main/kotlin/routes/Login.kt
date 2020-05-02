package routes

import db.data.UserCreateBody
import db.data.UserLoginRequest
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import org.koin.ktor.ext.inject
import service.login.LoginService
import utils.*

fun Routing.loginRoutes() {

    val loginService by inject<LoginService>()

    post(Endpoints.Login.Register) {
        val userLogin = call.receiveSafe<UserCreateBody>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        when (val loginResponse = loginService.register(userLogin)) {
            is ServiceResult.Success -> call.respond(HttpStatusCode.Created, loginResponse.data)
            is ServiceResult.Error -> respondError(loginResponse)
        }
    }

    post(Endpoints.Login.Refresh) {
        val userData = call.receiveSafe<UserLoginRequest>() ?: run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }
        when (val refreshResponse = loginService.login(userData)) {
            is ServiceResult.Success -> call.respond(HttpStatusCode.OK, refreshResponse.data)
            is ServiceResult.Error -> respondError(refreshResponse)
        }
    }
}