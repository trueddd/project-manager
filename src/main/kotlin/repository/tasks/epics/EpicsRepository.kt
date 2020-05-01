package repository.tasks.epics

import db.data.projects.Project
import db.data.tasks.Epic

interface EpicsRepository {

    fun getEpics(projectId: Int): List<Epic>

    fun createEpic(projectId: Int, name: String): Epic?

    fun renameEpic(epicId: Int, newName: String): Epic?

    fun deleteEpic(epicId: Int): Boolean

    fun getProjectByEpicId(epicId: Int): Project?
}