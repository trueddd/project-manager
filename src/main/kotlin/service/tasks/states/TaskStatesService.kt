package service.tasks.states

import db.data.User
import db.data.tasks.TaskState
import utils.ServiceResult

interface TaskStatesService {

    fun getAllTaskStates(user: User): ServiceResult<List<TaskState>>

    fun createTaskState(user: User, stateName: String): ServiceResult<TaskState>

    fun modifyTaskState(user: User, stateId: Int, stateName: String): ServiceResult<TaskState>

    fun deleteTaskState(user: User, stateId: Int): ServiceResult<Unit>
}