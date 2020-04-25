package route

import db.data.TeamCreateRequestBody
import db.data.TeamUpdateBody
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import service.teams.TeamsService
import utils.Endpoints
import utils.ServiceResult
import utils.receiveSafe
import utils.user

fun Routing.teamRoutes() {

    val teamsService by inject<TeamsService>()

    authenticate {

        get(Endpoints.Teams) {
            when (val teams = teamsService.getAllTeams()) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, teams.data)
                is ServiceResult.Error -> call.respond(HttpStatusCode.InternalServerError)
            }
        }

        post(Endpoints.Teams) {
            val currentUser = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val requested = call.receiveSafe<TeamCreateRequestBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            when (val owner = teamsService.registerTeam(currentUser.id, requested.name, requested.country, requested.city)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.Created, owner.data)
                is ServiceResult.Error -> call.respond(HttpStatusCode.InternalServerError, owner.e.message.orEmpty())
            }
        }

        put(Endpoints.Teams) {
            val currentUser = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@put
            }
            val toUpdate = call.receiveSafe<TeamUpdateBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            if (!teamsService.isUserFromTeam(currentUser.id, toUpdate.id)) {
                call.respond(HttpStatusCode.Unauthorized, "You are not allowed to edit this team")
                return@put
            }
            when (val updated = teamsService.modifyTeam(toUpdate.id, toUpdate.name, toUpdate.country, toUpdate.city)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, updated.data)
                is ServiceResult.Error -> call.respond(HttpStatusCode.InternalServerError, updated.e.message.orEmpty())
            }
        }

        delete(Endpoints.Teams) {
            val currentUser = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }
            val toDelete = call.parameters["id"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest, "Specify team you want to delete by sending its id as query-parameter")
                return@delete
            }
            if (!teamsService.isUserFromTeam(currentUser.id, toDelete)) {
                call.respond(HttpStatusCode.Unauthorized, "You are not allowed to delete this team")
                return@delete
            }
            when (teamsService.deleteTeam(toDelete)) {
                true -> call.respond(HttpStatusCode.OK)
                false -> call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}