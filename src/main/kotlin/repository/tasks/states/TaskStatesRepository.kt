package repository.tasks.states

import db.data.tasks.TaskState

interface TaskStatesRepository {

    fun getTaskStates(): List<TaskState>

    fun getTaskStateById(id: Int): TaskState?

    fun createTaskState(name: String): TaskState?

    fun renameTaskState(id: Int, name: String): TaskState?

    fun deleteTaskState(id: Int): Boolean
}