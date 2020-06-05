package repository.tasks.sprints

import db.dao.*
import db.data.User
import db.data.tasks.Sprint
import db.data.tasks.SprintCreateBody
import db.data.tasks.SprintUpdateBody
import org.jetbrains.exposed.sql.*
import repository.BaseRepository
import utils.toSprint

class SprintsRepositoryImpl(database: Database) : BaseRepository(database), SprintsRepository {

    override fun getSprints(projectId: Int, epicId: Int?): List<Sprint> = query {
        val epics = epicId?.let { listOf(it) } ?: Epics.select { Epics.projectId eq projectId }.map { it[Epics.id].toInt() }
        return@query Sprints.select { Sprints.epicId inList epics }.map { it.toSprint() }
    }

    override fun getSprintById(sprintId: Int): Sprint? = query {
        return@query Sprints.select { Sprints.id eq sprintId }.singleOrNull()?.toSprint()
    }

    override fun createSprint(epicId: Int, body: SprintCreateBody): Sprint? = query {
        return@query Sprints.insert {
            it[name] = body.name
            it[Sprints.epicId] = epicId
            it[start] = body.start
            it[finish] = body.finish
        }.resultedValues?.firstOrNull()?.toSprint()
    }

    override fun modifySprint(sprintId: Int, body: SprintUpdateBody): Sprint? = query {
        val updated = Sprints.update({ Sprints.id eq sprintId }) {
            body.name?.let { newValue -> it[name] = newValue }
            body.start?.let { newValue -> it[start] = newValue }
            body.finish?.let { newValue -> it[finish] = newValue }
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