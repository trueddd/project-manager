package repository.tasks.sprints

import db.dao.*
import db.data.User
import db.data.tasks.Sprint
import org.jetbrains.exposed.sql.*
import repository.BaseRepository
import utils.toSprint

class SprintsRepositoryImpl(database: Database) : BaseRepository(database), SprintsRepository {

    override fun getSprints(epicId: Int): List<Sprint> = query {
        return@query Sprints.select { Sprints.epicId eq epicId }.map { it.toSprint() }
    }

    override fun getSprintsByProject(projectId: Int): List<Sprint> = query {
        val epics = Epics.select { Epics.projectId eq projectId }.map { it[Epics.id].toInt() }
        return@query Sprints.select { Sprints.epicId inList epics }.map { it.toSprint() }
    }

    override fun getSprintById(sprintId: Int): Sprint? = query {
        return@query Sprints.select { Sprints.id eq sprintId }.singleOrNull()?.toSprint()
    }

    override fun createSprint(epicId: Int, name: String): Sprint? = query {
        return@query Sprints.insert {
            it[Sprints.name] = name
            it[Sprints.epicId] = epicId
        }.resultedValues?.firstOrNull()?.toSprint()
    }

    override fun renameSprint(sprintId: Int, newName: String): Sprint? = query {
        val updated = Sprints.update({ Sprints.id eq sprintId }) {
            it[name] = newName
        }
        if (updated <= 0) {
            return@query null
        }
        return@query Sprints.select { Sprints.id eq sprintId }.singleOrNull()?.toSprint()
    }

    override fun deleteSprint(sprintId: Int): Boolean = query {
        return@query Sprints.deleteWhere { Sprints.id eq sprintId } > 0
    }

    override fun getUserRightsOnSprint(user: User, sprintId: Int): Int = query {
        val sprint = Sprints.select { Sprints.id eq sprintId }.singleOrNull() ?: return@query -1
        val epic = Epics.select { Epics.id eq sprint[Sprints.epicId].toInt() }.singleOrNull() ?: return@query -1
        val project = Projects
            .select { Projects.id eq epic[Epics.projectId].toInt() }
            .singleOrNull() ?: return@query -1
        return@query ProjectsUsers
            .select { (ProjectsUsers.projectId eq project[Projects.id].toInt()) and (ProjectsUsers.userId eq user.id) }
            .singleOrNull()?.let { it[ProjectsUsers.rights].toInt() } ?: -1
    }
}