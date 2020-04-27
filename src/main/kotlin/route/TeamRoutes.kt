package route

import db.data.teams.TeamCreateRequestBody
import db.data.teams.TeamUpdateBody
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

        get(Endpoints.Teams.Base) {
            when (val teams = teamsService.getAllTeams()) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, teams.data)
                is ServiceResult.Error -> call.respond(HttpStatusCode.InternalServerError)
            }
        }

        get(Endpoints.Teams.Members) {
            val currentUser = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@get
            }
            val teamId = call.parameters["team_id"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest, "No team id provided")
                return@get
            }
            if (!teamsService.isUserFromTeam(currentUser.id, teamId)) {
                call.respond(HttpStatusCode.Unauthorized, "You are not allowed to get info of requested team")
                return@get
            }
            when (val users = teamsService.getTeamMembers(teamId)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, users.data)
                is ServiceResult.Error -> call.respond(HttpStatusCode.InternalServerError, users.e.message.orEmpty())
            }
        }

        post(Endpoints.Teams.Base) {
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

        put(Endpoints.Teams.Base) {
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

        delete(Endpoints.Teams.Base) {
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