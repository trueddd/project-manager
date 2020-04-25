package service.teams

import db.data.Team
import repository.teams.TeamsRepository
import utils.Errors
import utils.ServiceResult
import utils.error
import utils.success

class TeamsServiceImpl(private val teamsRepository: TeamsRepository) : TeamsService {

    override fun getAllTeams(): ServiceResult<List<Team>> {
        return teamsRepository.getAllTeams().success()
    }

    override fun registerTeam(name: String, country: String?, city: String?): ServiceResult<Team> {
        return teamsRepository.addNewTeam(name, country, city)?.success() ?: Errors.Create("Team").error()
    }

    override fun modifyTeam(teamId: Int, name: String?, country: String?, city: String?): ServiceResult<Team> {
        return teamsRepository.modifyTeam(teamId, name, country, city)?.success() ?: Errors.Modify("Team").error()
    }

    override fun deleteTeam(teamId: Int): Boolean {
        return teamsRepository.deleteTeam(teamId)
    }
}