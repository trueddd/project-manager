package service.tasks.sprints

import db.data.User
import db.data.tasks.Sprint
import utils.ServiceResult

interface SprintsService {

    fun getSprintsByEpic(user: User, epicId: Int): ServiceResult<List<Sprint>>

    fun getSprintsByProject(user: User, projectId: Int): ServiceResult<List<Sprint>>

    fun createSprint(user: User, epicId: Int, sprintName: String): ServiceResult<Sprint>

    fun renameSprint(user: User, sprintId: Int, newName: String): ServiceResult<Sprint>

    fun deleteSprint(user: User, sprintId: Int): ServiceResult<Unit>
}