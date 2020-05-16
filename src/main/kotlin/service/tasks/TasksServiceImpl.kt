package service.tasks

import db.data.User
import db.data.tasks.Task
import db.data.tasks.TaskCreateBody
import db.data.tasks.TaskUpdateBody
import repository.projects.ProjectsRepository
import repository.tasks.TasksRepository
import repository.tasks.epics.EpicsRepository
import repository.tasks.sprints.SprintsRepository
import utils.Errors
import utils.ServiceResult
import utils.error
import utils.success

class TasksServiceImpl(
    private val projectsRepository: ProjectsRepository,
    private val epicsRepository: EpicsRepository,
    private val sprintsRepository: SprintsRepository,
    private val tasksRepository: TasksRepository
) : TasksService {

    override fun getTasksByProject(user: User, projectId: Int): ServiceResult<List<Task>> {
        if (projectsRepository.getProjectById(projectId) == null) {
            return Errors.NotFound("project").error()
        }
        if (projectsRepository.getUserRightsOnProject(user, projectId) < 0) {
            return Errors.NoAccess("project").error()
        }
        return tasksRepository.getTaskByProject(projectId).success()
    }

    override fun getTasksByEpic(user: User, epicId: Int): ServiceResult<List<Task>> {
        if (epicsRepository.getEpicById(epicId) == null) {
            return Errors.NotFound("epic").error()
        }
        if (projectsRepository.getUserRightsOnEpic(user, epicId) < 0) {
            return Errors.NoAccess("epic").error()
        }
        return tasksRepository.getTasksByEpic(epicId).success()
    }

    override fun getTaskBySprint(user: User, sprintId: Int): ServiceResult<List<Task>> {
        if (sprintsRepository.getSprintById(sprintId) == null) {
            return Errors.NotFound("sprint").error()
        }
        if (projectsRepository.getUserRightsOnSprint(user, sprintId) < 0) {
            return Errors.NoAccess("sprint").error()
        }
        return tasksRepository.getTasksBySprint(sprintId).success()
    }

    override fun createTask(user: User, body: TaskCreateBody): ServiceResult<Task> {
        if (projectsRepository.getUserRightsOnSprint(user, body.sprintId) < 0) {
            return Errors.NoAccess("sprint").error()
        }
        return tasksRepository.createTask(user.id, body)?.success() ?: Errors.Unknown.error()
    }

    override fun modifyTask(user: User, taskId: Int, body: TaskUpdateBody): ServiceResult<Task> {
        if (tasksRepository.getTaskById(taskId) == null) {
            return Errors.NotFound("task").error()
        }
        if (projectsRepository.getUserRightsOnTask(user, taskId) < 0) {
            return Errors.NoAccess("task").error()
        }
        return tasksRepository.modifyTask(taskId, body)?.success() ?: Errors.Unknown.error()
    }

    override fun deleteTask(user: User, taskId: Int): ServiceResult<Unit> {
        if (tasksRepository.getTaskById(taskId) == null) {
            return Errors.NotFound("task").error()
        }
        if (projectsRepository.getUserRightsOnTask(user, taskId) < 0) {
            return Errors.NoAccess("task").error()
        }
        return if (tasksRepository.deleteTask(taskId)) Unit.success() else Errors.Unknown.error()
    }
}