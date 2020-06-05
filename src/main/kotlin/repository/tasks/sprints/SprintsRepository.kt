package repository.tasks.sprints

import db.data.User
import db.data.tasks.Sprint
import db.data.tasks.SprintCreateBody
import db.data.tasks.SprintUpdateBody

interface SprintsRepository {

    fun getSprints(projectId: Int, epicId: Int?): List<Sprint>

    fun getSprintById(sprintId: Int): Sprint?

    fun createSprint(epicId: Int, body: SprintCreateBody): Sprint?

    fun modifySprint(sprintId: Int, body: SprintUpdateBody): Sprint?

    fun deleteSprint(sprintId: Int): Boolean

    fun getUserRightsOnSprint(user: User, sprintId: Int): Int
}