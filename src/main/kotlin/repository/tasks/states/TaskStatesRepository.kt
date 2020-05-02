package repository.tasks.states

import db.data.tasks.TaskState

interface TaskStatesRepository {

    fun getTaskStates(teamId: Int? = null, onlyTeam: Boolean = false): List<TaskState>

    fun getTaskStateById(id: Int): TaskState?

    fun createTaskState(name: String, teamId: Int): TaskState?

    fun renameTaskState(id: Int, name: String): TaskState?

    fun deleteTaskState(id: Int): Boolean
}