package service.tasks

import db.data.User
import db.data.tasks.*
import utils.ServiceResult

interface TasksService {

    fun getTasksByProject(user: User, projectId: Int): ServiceResult<List<Task>>

    fun getTasksByEpic(user: User, epicId: Int): ServiceResult<List<Task>>

    fun getTaskBySprint(user: User, sprintId: Int): ServiceResult<List<Task>>

    fun createTask(user: User, body: TaskCreateBody): ServiceResult<Task>

    fun modifyTask(user: User, taskId: Int, body: TaskUpdateBody): ServiceResult<Task>

    fun deleteTask(user: User, taskId: Int): ServiceResult<Unit>

    fun createWorklog(user: User, taskId: Int, body: WorklogCreateBody): ServiceResult<Task>

    fun modifyWorklog(user: User, worklogId: Int, body: WorklogUpdateBody): ServiceResult<Task>

    fun deleteWorklog(user: User, worklogId: Int): ServiceResult<Unit>

    fun getUserWorklogsByTask(user: User, taskId: Int): ServiceResult<WorklogStats>

    fun getUserWorklogsBySprint(user: User, sprintId: Int): ServiceResult<WorklogStats>

    fun getUserWorklogsByEpic(user: User, epicId: Int): ServiceResult<WorklogStats>

    fun getUserWorklogsByProject(user: User, projectId: Int): ServiceResult<WorklogStats>

    fun getUserWorklogs(user: User): ServiceResult<WorklogStats>
}