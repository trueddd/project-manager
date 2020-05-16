package repository.tasks

import db.data.tasks.Task
import db.data.tasks.TaskCreateBody
import db.data.tasks.TaskUpdateBody

interface TasksRepository {

    fun getTasksBySprint(sprintId: Int): List<Task>

    fun getTasksByEpic(epicId: Int): List<Task>

    fun getTaskByProject(projectId: Int): List<Task>

    fun getTaskById(taskId: Int): Task?

    fun createTask(userId: Int, body: TaskCreateBody): Task?

    fun modifyTask(taskId: Int, updateBody: TaskUpdateBody): Task?

    fun deleteTask(taskId: Int): Boolean
}