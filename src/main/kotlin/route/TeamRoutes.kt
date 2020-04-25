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
import utils.receiveSafe

fun Routing.teamRoutes() {

    val teamsService by inject<TeamsService>()

    authenticate {

        get(Endpoints.Teams) {
            val teams = teamsService.getAllTeams()
            call.respond(HttpStatusCode.OK, teams)
        }

        post(Endpoints.Teams) {
            val requested = call.receiveSafe<TeamCreateRequestBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val newTeam = teamsService.registerTeam(requested.name, requested.country, requested.city)
            if (newTeam != null) {
                call.respond(HttpStatusCode.Created, newTeam)
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }

        put(Endpoints.Teams) {
            val toUpdate = call.receiveSafe<TeamUpdateBody>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@put
            }
            val updated = teamsService.modifyTeam(toUpdate.id, toUpdate.name, toUpdate.country, toUpdate.city)
            if (updated != null) {
                call.respond(HttpStatusCode.OK, updated)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        delete(Endpoints.Teams) {
            val toDelete = call.parameters["id"]?.toIntOrNull() ?: run {
                call.respond(HttpStatusCode.BadRequest, "Specify team you want to delete by sending its id as query-parameter")
                return@delete
            }
            val deleted = teamsService.deleteTeam(toDelete)
            if (deleted) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}