package service.teams

import db.data.Team
import utils.ServiceResult

interface TeamsService {

    fun getAllTeams(): ServiceResult<List<Team>>

    fun registerTeam(name: String, country: String? = null, city: String? = null): ServiceResult<Team>

    fun modifyTeam(teamId: Int, name: String? = null, country: String? = null, city: String? = null): ServiceResult<Team>

    fun deleteTeam(teamId: Int): Boolean
}