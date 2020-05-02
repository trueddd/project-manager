package service.tasks.sprints

import db.data.User
import db.data.tasks.Sprint
import repository.projects.ProjectsRepository
import repository.tasks.epics.EpicsRepository
import repository.tasks.sprints.SprintsRepository
import utils.Errors
import utils.ServiceResult
import utils.error
import utils.success

class SprintsServiceImpl(
    private val sprintsRepository: SprintsRepository,
    private val epicsRepository: EpicsRepository,
    private val projectsRepository: ProjectsRepository
) : SprintsService {

    override fun getSprintsByEpic(user: User, epicId: Int): ServiceResult<List<Sprint>> {
        if (user.team == null) {
            return Errors.NoAccess("epic").error()
        }
        if (epicsRepository.getEpicById(epicId) == null) {
            return Errors.NotFound("epic").error()
        }
        val project = epicsRepository.getProjectByEpicId(epicId) ?: return Errors.Unknown.error()
        if (!projectsRepository.isUserRelatedToProject(user, project.id)) {
            return Errors.NoAccess("epic").error()
        }
        return sprintsRepository.getSprints(epicId).success()
    }

    override fun getSprintsByProject(user: User, projectId: Int): ServiceResult<List<Sprint>> {
        if (user.team == null) {
            return Errors.NoAccess("project").error()
        }
        if (projectsRepository.getProjectById(projectId) == null) {
            return Errors.NotFound("project").error()
        }
        if (!projectsRepository.isUserRelatedToProject(user, projectId)) {
            return Errors.NoAccess("epic").error()
        }
        return sprintsRepository.getSprintsByProject(projectId).success()
    }

    override fun createSprint(user: User, epicId: Int, sprintName: String): ServiceResult<Sprint> {
        if (user.team == null) {
            return Errors.NoAccess("sprint").error()
        }
        if (epicsRepository.getEpicById(epicId) == null) {
            return Errors.NotFound("epic").error()
        }
        val project = epicsRepository.getProjectByEpicId(epicId) ?: return Errors.Unknown.error()
        if (!projectsRepository.isUserRelatedToProject(user, project.id)) {
            return Errors.NoAccess("epic").error()
        }
        return sprintsRepository.createSprint(epicId, sprintName)?.success() ?: Errors.Unknown.error()
    }

    override fun renameSprint(user: User, sprintId: Int, newName: String): ServiceResult<Sprint> {
        if (user.team == null) {
            return Errors.NoAccess("sprint").error()
        }
        if (sprintsRepository.getSprintById(sprintId) == null) {
            return Errors.NotFound("sprint").error()
        }
        if (!sprintsRepository.isUserRelatedToSprint(user, sprintId)) {
            return Errors.NoAccess("sprint").error()
        }
        return sprintsRepository.renameSprint(sprintId, newName)?.success() ?: Errors.Unknown.error()
    }

    override fun deleteSprint(user: User, sprintId: Int): ServiceResult<Unit> {
        if (user.team == null) {
            return Errors.NoAccess("sprint").error()
        }
        if (sprintsRepository.getSprintById(sprintId) == null) {
            return Errors.NotFound("sprint").error()
        }
        if (!sprintsRepository.isUserRelatedToSprint(user, sprintId)) {
            return Errors.NoAccess("sprint").error()
        }
        return if (sprintsRepository.deleteSprint(sprintId)) Unit.success() else Errors.Unknown.error()
    }
}