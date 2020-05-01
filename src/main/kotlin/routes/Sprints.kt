package routes

import db.data.tasks.NamedUpdateBody
import db.data.tasks.SprintCreateBody
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import service.tasks.sprints.SprintsService
import utils.*

fun Routing.sprintsRoutes() {

    val sprintsService by inject<SprintsService>()

    authenticate {

        get(Endpoints.Projects.Sprints) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            val epicId = call.parameters["epic_id"]?.toIntOrNull()
            val request = when {
                epicId != null -> sprintsService.getSprintsByEpic(user, epicId)
                projectId != null -> sprintsService.getSprintsByProject(user, projectId)
                else -> null
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            when (request) {
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

        post(Endpoints.Projects.Sprints) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val body = call.receiveSafe<SprintCreateBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            when (val request = sprintsService.createSprint(user, body.epicId, body.name)) {
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

        put(Endpoints.Projects.Sprints.path("sprint_id")) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@put
            }
            val sprintId = call.parameters["sprint_id"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            val body = call.receiveSafe<NamedUpdateBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            when (val request = sprintsService.renameSprint(user, sprintId, body.name)) {
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

        delete(Endpoints.Projects.Sprints.path("sprint_id")) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }
            val sprintId = call.parameters["sprint_id"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            when (val request = sprintsService.deleteSprint(user, sprintId)) {
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