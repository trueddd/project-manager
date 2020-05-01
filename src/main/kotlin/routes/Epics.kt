package routes

import db.data.tasks.EpicCreateBody
import db.data.tasks.NamedUpdateBody
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import service.tasks.epics.EpicsService
import utils.*

fun Routing.epicsRoutes() {

    val epicsService by inject<EpicsService>()

    authenticate {

        get(Endpoints.Projects.Epics.path("project_id")) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            val projectId = call.parameters["project_id"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            when (val request = epicsService.getEpics(user, projectId)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, request.data)
                is ServiceResult.Error -> {
                    val message = request.e.message.orEmpty()
                    when (request.e) {
                        is Errors.NotFound -> call.respond(HttpStatusCode.NotFound, message)
                        is Errors.NoAccess -> call.respond(HttpStatusCode.Unauthorized, message)
                    }
                }
            }
        }

        post(Endpoints.Projects.Epics) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val body = call.receiveSafe<EpicCreateBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            when (val request = epicsService.createEpic(user, body.projectId, body.name)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.Created, request.data)
                is ServiceResult.Error -> {
                    val message = request.e.message.orEmpty()
                    when (request.e) {
                        is Errors.NotFound -> call.respond(HttpStatusCode.NotFound, message)
                        is Errors.NoAccess -> call.respond(HttpStatusCode.Unauthorized, message)
                        else -> call.respond(HttpStatusCode.InternalServerError, message)
                    }
                }
            }
        }

        put(Endpoints.Projects.Epics.path("epic_id")) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@put
            }
            val epicId = call.parameters["epic_id"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            val body = call.receiveSafe<NamedUpdateBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            when (val request = epicsService.modifyEpic(user, epicId, body.name)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, request.data)
                is ServiceResult.Error -> {
                    val message = request.e.message.orEmpty()
                    when (request.e) {
                        is Errors.NotFound -> call.respond(HttpStatusCode.NotFound, message)
                        is Errors.NoAccess -> call.respond(HttpStatusCode.Unauthorized, message)
                        else -> call.respond(HttpStatusCode.InternalServerError, message)
                    }
                }
            }
        }

        delete(Endpoints.Projects.Epics.path("epic_id")) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }
            val epicId = call.parameters["epic_id"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            when (val request = epicsService.deleteEpic(user, epicId)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK)
                is ServiceResult.Error -> {
                    val message = request.e.message.orEmpty()
                    when (request.e) {
                        is Errors.NotFound -> call.respond(HttpStatusCode.NotFound, message)
                        is Errors.NoAccess -> call.respond(HttpStatusCode.Unauthorized, message)
                        else -> call.respond(HttpStatusCode.InternalServerError, message)
                    }
                }
            }
        }
    }
}