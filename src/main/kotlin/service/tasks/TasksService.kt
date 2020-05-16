package service.tasks

import db.data.User
import db.data.tasks.Task
import db.data.tasks.TaskCreateBody
import db.data.tasks.TaskUpdateBody
import utils.ServiceResult

interface TasksService {

    fun getTasksByProject(user: User, projectId: Int): ServiceResult<List<Task>>

    fun getTasksByEpic(user: User, epicId: Int): ServiceResult<List<Task>>

    fun getTaskBySprint(user: User, sprintId: Int): ServiceResult<List<Task>>

    fun createTask(user: User, body: TaskCreateBody): ServiceResult<Task>

    fun modifyTask(user: User, taskId: Int, body: TaskUpdateBody): ServiceResult<Task>

    fun deleteTask(user: User, taskId: Int): ServiceResult<Unit>
}