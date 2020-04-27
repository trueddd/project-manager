package repository.projects

import db.dao.Projects
import db.dao.Teams
import db.data.projects.Project
import db.data.teams.Team
import org.jetbrains.exposed.sql.*
import repository.BaseRepository

class ProjectsRepositoryImpl(database: Database) : BaseRepository(database), ProjectsRepository {

    override fun getTeamProjects(teamId: Int): List<Project> = query {
        Projects.select { Projects.teamId eq teamId }.map { it.toProject(withTeam = false) }
    }

    override fun createProject(teamId: Int, name: String): Project? = query {
        Projects.insert {
            it[Projects.name] = name
            it[Projects.teamId] = teamId
            it[createdAt] = System.currentTimeMillis()
        }.resultedValues?.firstOrNull()?.toProject(withTeam = false)
    }

    override fun modifyProject(projectId: Int, name: String): Project? = query {
        val modified = Projects.update({ Projects.id eq projectId }) {
            it[Projects.name] = name
        }
        if (modified < 1) {
            rollback()
            return@query null
        }
        return@query Projects.select { Projects.id eq projectId }.singleOrNull()?.toProject(withTeam = false)
    }

    override fun deleteProject(projectId: Int): Boolean = query {
        Projects.deleteWhere { Projects.id eq projectId } > 0
    }

    override fun isProjectFromTeam(projectId: Int, teamId: Int): Boolean = query {
        val project = (Projects leftJoin Teams)
            .select { Projects.id eq projectId }.singleOrNull()?.toProject()
        return@query project?.team?.id == teamId
    }

    private fun ResultRow.toProject(withTeam: Boolean = true): Project {
        return Project(
            this[Projects.id].toInt(),
            this[Projects.name].toString(),
            this[Projects.createdAt].toLong(),
            if (withTeam) {
                Team(
                    this[Teams.id].toInt(),
                    this[Teams.name].toString(),
                    this[Teams.country]?.toString(),
                    this[Teams.city]?.toString()
                )
            } else null
        )
    }
}