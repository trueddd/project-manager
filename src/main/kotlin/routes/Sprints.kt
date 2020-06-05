package routes

import db.data.tasks.SprintCreateBody
import db.data.tasks.SprintUpdateBody
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

        get(Endpoints.Projects.path("projectId").route(Endpoints.Epics).route(Endpoints.Sprints)) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            val projectId = call.parameters["projectId"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            val epicId = call.parameters["epic_id"]?.toIntOrNull()
            when (val request = sprintsService.getSprints(user, projectId, epicId)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        post(Endpoints.Projects.route(Endpoints.Epics).path("epicId").route(Endpoints.Sprints)) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val epicId = call.parameters["epicId"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val body = call.receiveSafe<SprintCreateBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            when (val request = sprintsService.createSprint(user, epicId, body)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.Created, request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        put(Endpoints.Projects.route(Endpoints.Epics).route(Endpoints.Sprints).path("sprint_id")) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@put
            }
            val sprintId = call.parameters["sprint_id"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            val body = call.receiveSafe<SprintUpdateBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            when (val request = sprintsService.modifySprint(user, sprintId, body)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        delete(Endpoints.Projects.route(Endpoints.Epics).route(Endpoints.Sprints).path("sprint_id")) {
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
                is ServiceResult.Error -> respondError(request)
            }
        }
    }
}