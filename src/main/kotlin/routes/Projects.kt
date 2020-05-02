package routes

import db.data.projects.ProjectBody
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import service.projects.ProjectsService
import utils.*

fun Routing.projectsRoutes() {

    val projectsService by inject<ProjectsService>()

    authenticate {

        get(Endpoints.Projects.Base) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            when (val request = projectsService.getTeamProjects(user)) {
                is ServiceResult.Success -> call.respond(request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        post(Endpoints.Projects.Base) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val projectRequest = call.receiveSafe<ProjectBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            when (val request = projectsService.createProject(user, projectRequest.name)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.Created, request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        put(Endpoints.Projects.Base.path("id")) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@put
            }
            val projectId = call.parameters["id"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            val projectRequest = call.receiveSafe<ProjectBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            when (val request = projectsService.modifyProject(user, projectId, projectRequest.name)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        delete(Endpoints.Projects.Base.path("id")) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }
            val projectId = call.parameters["id"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest, "No project id provided")
                return@delete
            }
            when (val request = projectsService.deleteProject(user, projectId)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK)
                is ServiceResult.Error -> respondError(request)
            }
        }
    }
}