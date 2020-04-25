package service.teams

import db.data.Team
import repository.teams.TeamsRepository

class TeamsServiceImpl(private val teamsRepository: TeamsRepository) : TeamsService {

    override fun getAllTeams(): List<Team> {
        return teamsRepository.getAllTeams()
    }

    override fun registerTeam(name: String, country: String?, city: String?): Team? {
        return teamsRepository.addNewTeam(name, country, city)
    }

    override fun modifyTeam(teamId: Int, name: String?, country: String?, city: String?): Team? {
        return teamsRepository.modifyTeam(teamId, name, country, city)
    }

    override fun deleteTeam(teamId: Int): Boolean {
        return teamsRepository.deleteTeam(teamId)
    }
}