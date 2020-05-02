package routes

import db.data.UserUpdateBody
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.put
import org.koin.ktor.ext.inject
import service.users.UsersService
import utils.*

fun Routing.usersRoutes() {

    val usersService by inject<UsersService>()

    authenticate {

        get(Endpoints.Users.Base) {
            when (val users = usersService.getAllUsers()) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, users.data)
                is ServiceResult.Error -> call.respond(HttpStatusCode.InternalServerError)
            }
        }

        put(Endpoints.Users.Base) {
            val currentUser = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@put
            }
            val toUpdate = call.receiveSafe<UserUpdateBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            when (val result = usersService.modifyUser(currentUser.id, toUpdate.name, toUpdate.firstName, toUpdate.lastName)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is ServiceResult.Error -> respondError(result)
            }
        }

        delete(Endpoints.Users.Base) {
            val currentUser = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }
            when (usersService.deleteUser(currentUser.id)) {
                true -> call.respond(HttpStatusCode.OK)
                false -> call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}