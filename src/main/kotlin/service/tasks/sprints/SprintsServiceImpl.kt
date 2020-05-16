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

    override fun getSprints(user: User, projectId: Int, epicId: Int?): ServiceResult<List<Sprint>> {
        if (projectsRepository.getProjectById(projectId) == null) {
            return Errors.NotFound("project").error()
        }
        epicId?.let {
            if (epicsRepository.getEpicById(it) == null) {
                return Errors.NotFound("epic").error()
            }
        }
        if (projectsRepository.getUserRightsOnProject(user, projectId) < 0) {
            return Errors.NoAccess("project").error()
        }
        return sprintsRepository.getSprints(projectId, epicId).success()
    }

    override fun createSprint(user: User, epicId: Int, sprintName: String): ServiceResult<Sprint> {
        if (epicsRepository.getEpicById(epicId) == null) {
            return Errors.NotFound("epic").error()
        }
        val project = epicsRepository.getProjectByEpicId(epicId) ?: return Errors.Unknown.error()
        if (projectsRepository.getUserRightsOnProject(user, project.id) < 0) {
            return Errors.NoAccess("epic").error()
        }
        return sprintsRepository.createSprint(epicId, sprintName)?.success() ?: Errors.Unknown.error()
    }

    override fun renameSprint(user: User, sprintId: Int, newName: String): ServiceResult<Sprint> {
        if (sprintsRepository.getSprintById(sprintId) == null) {
            return Errors.NotFound("sprint").error()
        }
        if (sprintsRepository.getUserRightsOnSprint(user, sprintId) < 0) {
            return Errors.NoAccess("sprint").error()
        }
        return sprintsRepository.renameSprint(sprintId, newName)?.success() ?: Errors.Unknown.error()
    }

    override fun deleteSprint(user: User, sprintId: Int): ServiceResult<Unit> {
        if (sprintsRepository.getSprintById(sprintId) == null) {
            return Errors.NotFound("sprint").error()
        }
        if (sprintsRepository.getUserRightsOnSprint(user, sprintId) < 0) {
            return Errors.NoAccess("sprint").error()
        }
        return if (sprintsRepository.deleteSprint(sprintId)) Unit.success() else Errors.Unknown.error()
    }
}