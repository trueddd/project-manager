package route

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import org.koin.ktor.ext.inject
import service.users.UsersService
import utils.Endpoints
import utils.ServiceResult

fun Routing.userRoutes() {

    val usersService by inject<UsersService>()

    authenticate {
        get(Endpoints.Users.Base) {
            when (val users = usersService.getAllUsers()) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, users.data)
                is ServiceResult.Error -> call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}