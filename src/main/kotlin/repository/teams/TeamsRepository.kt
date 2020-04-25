package repository.teams

import db.data.Team

interface TeamsRepository {

    fun getAllTeams(): List<Team>

    fun findTeamById(teamId: Int): Team?

    fun addNewTeam(name: String, country: String? = null, city: String? = null): Team?

    fun modifyTeam(teamId: Int, name: String? = null, country: String? = null, city: String? = null): Team?

    fun deleteTeam(id: Int): Boolean
}