package repository.tasks.sprints

import db.data.User
import db.data.tasks.Sprint

interface SprintsRepository {

    fun getSprints(epicId: Int): List<Sprint>

    fun getSprintsByProject(projectId: Int): List<Sprint>

    fun getSprintById(sprintId: Int): Sprint?

    fun createSprint(epicId: Int, name: String): Sprint?

    fun renameSprint(sprintId: Int, newName: String): Sprint?

    fun deleteSprint(sprintId: Int): Boolean

    fun isUserRelatedToSprint(user: User, sprintId: Int): Boolean
}