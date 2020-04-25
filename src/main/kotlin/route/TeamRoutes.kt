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
            val requested = call.receiveSafe<TeamCreateRequestBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            when (val newTeam = teamsService.registerTeam(requested.name, requested.country, requested.city)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.Created, newTeam.data)
                is ServiceResult.Error -> call.respond(HttpStatusCode.InternalServerError, newTeam.e.message.orEmpty())
            }
        }

        put(Endpoints.Teams) {
            val toUpdate = call.receiveSafe<TeamUpdateBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            when (val updated = teamsService.modifyTeam(toUpdate.id, toUpdate.name, toUpdate.country, toUpdate.city)) {
                is ServiceResult.Success -> call.respond(HttpStatusCode.OK, updated.data)
                is ServiceResult.Error -> call.respond(HttpStatusCode.InternalServerError, updated.e.message.orEmpty())
            }
        }

        delete(Endpoints.Teams) {
            val toDelete = call.parameters["id"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest, "Specify team you want to delete by sending its id as query-parameter")
                return@delete
            }
            when (teamsService.deleteTeam(toDelete)) {
                true -> call.respond(HttpStatusCode.OK)
                false -> call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}