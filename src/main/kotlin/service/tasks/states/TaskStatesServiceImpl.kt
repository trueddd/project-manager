package service.tasks.states

import db.data.User
import db.data.tasks.TaskState
import repository.tasks.states.TaskStatesRepository
import utils.Errors
import utils.ServiceResult
import utils.error
import utils.success

class TaskStatesServiceImpl(
    private val taskStatesRepository: TaskStatesRepository
) : TaskStatesService {

    override fun getAllTaskStates(user: User): ServiceResult<List<TaskState>> {
        return taskStatesRepository.getTaskStates(user.team?.id).success()
    }

    override fun createTaskState(user: User, stateName: String): ServiceResult<TaskState> {
        if (user.team == null) {
            return Errors.NotFound("Team").error()
        }
        return taskStatesRepository.createTaskState(stateName, user.team.id)?.success() ?: Errors.Unknown.error()
    }

    override fun modifyTaskState(user: User, stateId: Int, stateName: String): ServiceResult<TaskState> {
        if (user.team == null) {
            return Errors.NoAccess("task state").error()
        }
        if (taskStatesRepository.getTaskStateById(stateId) == null) {
            return Errors.NotFound("task state").error()
        }
        if (taskStatesRepository.getTaskStates(user.team.id, true).none { it.id == stateId }) {
            return Errors.NoAccess("task state").error()
        }
        return taskStatesRepository.renameTaskState(stateId, stateName)?.success() ?: Errors.Unknown.error()
    }

    override fun deleteTaskState(user: User, stateId: Int): ServiceResult<Unit> {
        if (user.team == null) {
            return Errors.NoAccess("task state").error()
        }
        if (taskStatesRepository.getTaskStateById(stateId) == null) {
            return Errors.NotFound("task state").error()
        }
        if (taskStatesRepository.getTaskStates(user.team.id, true).none { it.id == stateId }) {
            return Errors.NoAccess("task state").error()
        }
        val deleted = taskStatesRepository.deleteTaskState(stateId)
        return if (deleted) Unit.success() else Errors.Unknown.error()
    }
}