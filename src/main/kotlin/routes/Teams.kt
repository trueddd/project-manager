package routes

import db.data.teams.TeamBody
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import service.teams.TeamsService
import utils.*

fun Routing.teamsRoutes() {

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
            if (currentUser.team == null) {
                call.respond(HttpStatusCode.NotFound, "You have no team")
                return@get
            }
            when (val users = teamsService.getTeamMembers(currentUser.team.id)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, users.data)
                is ServiceResult.Error -> call.respond(HttpStatusCode.InternalServerError, users.e.message.orEmpty())
            }
        }

        post(Endpoints.Teams.Base) {
            val currentUser = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }
            val requested = call.receiveSafe<TeamBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            when (val owner = teamsService.registerTeam(currentUser.id, requested.name, requested.country, requested.city)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.Created, owner.data)
                is ServiceResult.Error -> call.respond(HttpStatusCode.InternalServerError, owner.e.message.orEmpty())
            }
        }

        put(Endpoints.Teams.Base.path("id")) {
            val currentUser = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@put
            }
            val toUpdate = call.receiveSafe<TeamBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            if (currentUser.team == null) {
                call.respond(HttpStatusCode.NotFound, "You have no team")
                return@put
            }
            when (val updated = teamsService.modifyTeam(currentUser.team.id, toUpdate.name, toUpdate.country, toUpdate.city)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, updated.data)
                is ServiceResult.Error -> call.respond(HttpStatusCode.InternalServerError, updated.e.message.orEmpty())
            }
        }

        delete(Endpoints.Teams.Base.path("id")) {
            val currentUser = call.user ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@delete
            }
            if (currentUser.team == null) {
                call.respond(HttpStatusCode.NotFound, "You have no team")
                return@delete
            }
            when (teamsService.deleteTeam(currentUser.team.id)) {
                true -> call.respond(HttpStatusCode.OK)
                false -> call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}