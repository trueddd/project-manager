package route

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import org.koin.ktor.ext.inject
import service.users.UsersService
import utils.Endpoints

fun Routing.userRoutes() {

    val usersService by inject<UsersService>()

    authenticate {
        get(Endpoints.Users.Base) {
            val users = usersService.getAllUsers()
            call.respond(users)
        }
    }
}