package service.tasks.epics

import db.data.User
import db.data.tasks.Epic
import utils.ServiceResult

interface EpicsService {

    fun getEpics(user: User, projectId: Int): ServiceResult<List<Epic>>

    fun createEpic(user: User, projectId: Int, epicName: String): ServiceResult<Epic>

    fun modifyEpic(user: User, epicId: Int, newName: String): ServiceResult<Epic>

    fun deleteEpic(user: User, epicId: Int): ServiceResult<Unit>
}