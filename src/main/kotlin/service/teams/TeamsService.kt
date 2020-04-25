package service.teams

import db.data.Team

interface TeamsService {

    fun getAllTeams(): List<Team>

    fun registerTeam(name: String, country: String? = null, city: String? = null): Team?

    fun modifyTeam(teamId: Int, name: String? = null, country: String? = null, city: String? = null): Team?

    fun deleteTeam(teamId: Int): Boolean
}