package routes

import db.data.tasks.TaskStateBody
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import service.tasks.states.TaskStatesService
import utils.*

fun Routing.taskStatesRoutes() {

    val taskStatesService by inject<TaskStatesService>()

    authenticate {

        get(Endpoints.Tasks.route(Endpoints.States)) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            when (val request = taskStatesService.getAllTaskStates(user)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, request.data)
                is ServiceResult.Error -> call.respond(HttpStatusCode.InternalServerError, request.errorMessage())
            }
        }

        post(Endpoints.Tasks.route(Endpoints.States)) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val body = call.receiveSafe<TaskStateBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            when (val request = taskStatesService.createTaskState(user, body.name)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.Created, request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        put(Endpoints.Tasks.route(Endpoints.States).path("id")) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@put
            }
            val stateId = call.parameters["id"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            val body = call.receiveSafe<TaskStateBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            when (val request = taskStatesService.modifyTaskState(user, stateId, body.name)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        delete(Endpoints.Tasks.route(Endpoints.States).path("id")) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }
            val stateId = call.parameters["id"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            when (val request = taskStatesService.deleteTaskState(user, stateId)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK)
                is ServiceResult.Error -> respondError(request)
            }
        }
    }
}