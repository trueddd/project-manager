package service.tasks.sprints

import db.data.User
import db.data.tasks.Sprint
import db.data.tasks.SprintCreateBody
import db.data.tasks.SprintUpdateBody
import utils.ServiceResult

interface SprintsService {

    fun getSprints(user: User, projectId: Int, epicId: Int?): ServiceResult<List<Sprint>>

    fun createSprint(user: User, epicId: Int, body: SprintCreateBody): ServiceResult<Sprint>

    fun modifySprint(user: User, sprintId: Int, body: SprintUpdateBody): ServiceResult<Sprint>

    fun deleteSprint(user: User, sprintId: Int): ServiceResult<Unit>
}