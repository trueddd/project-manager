package repository.tasks.sprints

import db.data.User
import db.data.tasks.Sprint

interface SprintsRepository {

    fun getSprints(projectId: Int, epicId: Int?): List<Sprint>

    fun getSprintById(sprintId: Int): Sprint?

    fun createSprint(epicId: Int, name: String): Sprint?

    fun renameSprint(sprintId: Int, newName: String): Sprint?

    fun deleteSprint(sprintId: Int): Boolean

    fun getUserRightsOnSprint(user: User, sprintId: Int): Int
}