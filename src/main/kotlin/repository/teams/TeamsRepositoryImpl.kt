package repository.teams

import db.dao.Teams
import db.dao.Users
import db.data.Team
import org.jetbrains.exposed.sql.*
import repository.BaseRepository

class TeamsRepositoryImpl(database: Database) : BaseRepository(database), TeamsRepository {

    override fun getAllTeams(): List<Team> {
        return query {
            Teams.selectAll().map { it.toTeam() }
        }
    }

    override fun findTeamById(teamId: Int): Team? {
        return query {
            Teams.select { Teams.id eq teamId }.singleOrNull()?.toTeam()
        }
    }

    override fun addNewTeam(name: String, country: String?, city: String?): Team? {
        return query {
            Teams.insert {
                it[Teams.name] = name
                it[Teams.country] = country
                it[Teams.city] = city
            }.resultedValues?.firstOrNull()?.toTeam()
        }
    }

    override fun modifyTeam(teamId: Int, name: String?, country: String?, city: String?): Team? = query {
        val affected = Teams.update({ Teams.id eq teamId }) {
            name?.let { teamNewName -> it[Teams.name] = teamNewName }
            country?.let { teamNewCountry -> it[Teams.country] = teamNewCountry }
            city?.let { teamNewCity -> it[Teams.city] = teamNewCity }
        }
        if (affected <= 0) {
            return@query null
        }
        Teams.select { Users.id eq teamId }.singleOrNull()?.toTeam()
    }

    override fun deleteTeam(id: Int): Boolean {
        return query {
            Teams.deleteWhere { Teams.id eq id } > 0
        }
    }

    private fun ResultRow.toTeam(): Team {
        return Team(
            this[Teams.id].toInt(),
            this[Teams.name].toString(),
            this[Teams.country]?.toString(),
            this[Teams.city]?.toString()
        )
    }
}