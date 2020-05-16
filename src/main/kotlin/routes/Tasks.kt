package routes

import db.data.tasks.TaskCreateBody
import db.data.tasks.TaskUpdateBody
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import service.tasks.TasksService
import utils.*

fun Routing.taskRoutes() {

    val tasksService by inject<TasksService>()

    authenticate {

        get(Endpoints.Tasks) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            val projectId = call.parameters["project_id"]?.toIntOrNull()
            val epicId = call.parameters["epic_id"]?.toIntOrNull()
            val sprintId = call.parameters["sprint_id"]?.toIntOrNull()
            val request = when {
                sprintId != null -> tasksService.getTaskBySprint(user, sprintId)
                epicId != null -> tasksService.getTasksByEpic(user, epicId)
                projectId != null -> tasksService.getTasksByProject(user, projectId)
                else -> null
            } ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }
            when (request) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        post(Endpoints.Tasks) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val body = call.receiveSafe<TaskCreateBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            when (val request = tasksService.createTask(user, body)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.Created, request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        put(Endpoints.Tasks.path("taskId")) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@put
            }
            val taskId = call.parameters["taskId"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            val body = call.receiveSafe<TaskUpdateBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            when (val request = tasksService.modifyTask(user, taskId, body)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }

        delete(Endpoints.Tasks.path("taskId")) {
            val user = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }
            val taskId = call.parameters["taskId"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }
            when (val request = tasksService.deleteTask(user, taskId)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, request.data)
                is ServiceResult.Error -> respondError(request)
            }
        }
    }
}