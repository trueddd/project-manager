package service.teams

import db.data.Team
import db.data.User
import repository.teams.TeamsRepository
import repository.users.UsersRepository
import utils.Errors
import utils.ServiceResult
import utils.error
import utils.success

class TeamsServiceImpl(
    private val teamsRepository: TeamsRepository,
    private val usersRepository: UsersRepository
) : TeamsService {

    override fun getAllTeams(): ServiceResult<List<Team>> {
        return teamsRepository.getAllTeams().success()
    }

    override fun isUserFromTeam(userId: Int, teamId: Int): Boolean {
        val user = usersRepository.findUserById(userId) ?: return false
        return user.team?.id == teamId
    }

    override fun registerTeam(ownerId: Int, name: String, country: String?, city: String?): ServiceResult<User> {
        teamsRepository.addNewTeam(ownerId, name, country, city) ?: return Errors.Create("Team").error()
        return usersRepository.findUserById(ownerId)?.success() ?: Errors.Teams.OwnerApply.error()
    }

    override fun modifyTeam(teamId: Int, name: String?, country: String?, city: String?): ServiceResult<Team> {
        return teamsRepository.modifyTeam(teamId, name, country, city)?.success() ?: Errors.Modify("Team").error()
    }

    override fun deleteTeam(teamId: Int): Boolean {
        return teamsRepository.deleteTeam(teamId)
    }
}