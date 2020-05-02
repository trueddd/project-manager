package repository.tasks.epics

import db.dao.Epics
import db.dao.Projects
import db.data.projects.Project
import db.data.tasks.Epic
import org.jetbrains.exposed.sql.*
import repository.BaseRepository
import utils.toEpic
import utils.toProject

class EpicsRepositoryImpl(database: Database) : BaseRepository(database), EpicsRepository {

    override fun getEpics(projectId: Int): List<Epic> = query {
        return@query Epics.select { Epics.projectId eq projectId }.map { it.toEpic() }
    }

    override fun createEpic(projectId: Int, name: String): Epic? = query {
        return@query Epics.insert {
            it[Epics.name] = name
            it[Epics.projectId] = projectId
        }.resultedValues?.firstOrNull()?.toEpic()
    }

    override fun renameEpic(epicId: Int, newName: String): Epic? = query {
        val updated = Epics.update({ Epics.id eq epicId }) {
            it[name] = newName
        }
        if (updated <= 0) {
            return@query null
        }
        return@query Epics.select { Epics.id eq epicId }.singleOrNull()?.toEpic()
    }

    override fun deleteEpic(epicId: Int): Boolean = query {
        return@query Epics.deleteWhere { Epics.id eq epicId } > 0
    }

    override fun getProjectByEpicId(epicId: Int): Project? = query {
        val epic = Epics.select { Epics.id eq epicId }.singleOrNull() ?: return@query null
        return@query Projects
            .select { Projects.id eq epic[Epics.projectId].toInt() }
            .singleOrNull()?.toProject(false)
    }

    override fun getEpicById(epicId: Int): Epic? = query {
        return@query Epics.select { Epics.id eq epicId }.singleOrNull()?.toEpic()
    }
}