package repository.tasks

import db.data.User
import db.data.tasks.*

interface TasksRepository {

    fun getTasksBySprint(sprintId: Int): List<Task>

    fun getTasksByEpic(epicId: Int): List<Task>

    fun getTaskByProject(projectId: Int): List<Task>

    fun getTaskById(taskId: Int): Task?

    fun createTask(userId: Int, body: TaskCreateBody): Task?

    fun modifyTask(taskId: Int, updateBody: TaskUpdateBody): Task?

    fun deleteTask(taskId: Int): Boolean

    fun getUserRightsOnTask(user: User, taskId: Int): Int

    fun getWorklogById(worklogId: Int): Worklog?

    fun createWorklog(userId: Int, taskId: Int, createBody: WorklogCreateBody): Task?

    fun modifyWorklog(worklogId: Int, updateBody: WorklogUpdateBody): Task?

    fun isUserWorklog(userId: Int, worklogId: Int): Boolean

    fun deleteWorklog(worklogId: Int): Boolean
}