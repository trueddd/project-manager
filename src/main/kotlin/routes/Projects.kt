package routes

import db.data.projects.ProjectCreateBody
import db.data.projects.ProjectUpdateBody
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

        get(Endpoints.Projects) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            when (val request = projectsService.getTeamProjects(user)) {
                is ServiceResult.Success -> call.respond(request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        post(Endpoints.Projects) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val projectRequest = call.receiveSafe<ProjectCreateBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            when (val request = projectsService.createProject(user, projectRequest)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.Created, request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        put(Endpoints.Projects.path("id")) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@put
            }
            val projectId = call.parameters["id"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            val projectRequest = call.receiveSafe<ProjectUpdateBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            when (val request = projectsService.modifyProject(user, projectId, projectRequest)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        delete(Endpoints.Projects.path("id")) {
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

        get(Endpoints.Projects.path("id").route(Endpoints.Users)) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            val projectId = call.parameters["id"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest, "No project id provided")
                return@get
            }
            when (val request = projectsService.getProjectMembers(user, projectId)) {
                is ServiceResult.Success -> call.respond(request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        post(Endpoints.Projects.path("projectId").route(Endpoints.Users).path("userId")) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val projectId = call.parameters["projectId"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest, "No project id provided")
                return@post
            }
            val userId = call.parameters["userId"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest, "No user id provided")
                return@post
            }
            when (val request = projectsService.addProjectMember(user, projectId, userId)) {
                is ServiceResult.Success -> call.respond(request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        delete(Endpoints.Projects.path("projectId").route(Endpoints.Users).path("userId")) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }
            val projectId = call.parameters["projectId"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest, "No project id provided")
                return@delete
            }
            val userId = call.parameters["userId"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest, "No user id provided")
                return@delete
            }
            when (val request = projectsService.removeProjectMember(user, projectId, userId)) {
                is ServiceResult.Success -> call.respond(request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        get(Endpoints.Projects.path("projectId").route(Endpoints.Worklogs)) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            val projectId = call.parameters["projectId"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest, "No project id provided")
                return@get
            }
            when (val request = projectsService.getProjectWorklogs(user, projectId)) {
                is ServiceResult.Success -> call.respond(request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }
    }
}