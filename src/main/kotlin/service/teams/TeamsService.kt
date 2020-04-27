package service.teams

import db.data.teams.Team
import db.data.User
import utils.ServiceResult

interface TeamsService {

    fun getAllTeams(): ServiceResult<List<Team>>

    fun getTeamMembers(teamId: Int): ServiceResult<List<User>>

    fun isUserFromTeam(userId: Int, teamId: Int): Boolean

    fun registerTeam(ownerId: Int, name: String, country: String? = null, city: String? = null): ServiceResult<User>

    fun modifyTeam(teamId: Int, name: String? = null, country: String? = null, city: String? = null): ServiceResult<Team>

    fun deleteTeam(teamId: Int): Boolean
}