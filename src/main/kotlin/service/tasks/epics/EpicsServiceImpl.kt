package service.tasks.epics

import db.data.User
import db.data.tasks.Epic
import repository.projects.ProjectsRepository
import repository.tasks.epics.EpicsRepository
import utils.Errors
import utils.ServiceResult
import utils.error
import utils.success

class EpicsServiceImpl(
    private val epicsRepository: EpicsRepository,
    private val projectsRepository: ProjectsRepository
) : EpicsService {

    override fun getEpics(user: User, projectId: Int): ServiceResult<List<Epic>> {
        if (projectsRepository.getProjectById(projectId) == null) {
            return Errors.NotFound("project").error()
        }
        if (projectsRepository.getUserRightsOnProject(user, projectId) < 0) {
            return Errors.NoAccess("project").error()
        }
        return epicsRepository.getEpics(projectId).success()
    }

    override fun createEpic(user: User, projectId: Int, epicName: String): ServiceResult<Epic> {
        if (projectsRepository.getProjectById(projectId) == null) {
            return Errors.NotFound("project").error()
        }
        if (projectsRepository.getUserRightsOnProject(user, projectId) < 0) {
            return Errors.NoAccess("project").error()
        }
        return epicsRepository.createEpic(projectId, epicName)?.success() ?: Errors.Unknown.error()
    }

    override fun modifyEpic(user: User, epicId: Int, newName: String): ServiceResult<Epic> {
        if (epicsRepository.getEpicById(epicId) == null) {
            return Errors.NotFound("epic").error()
        }
        val project = epicsRepository.getProjectByEpicId(epicId) ?: return Errors.Unknown.error()
        if (projectsRepository.getUserRightsOnProject(user, project.id) < 0) {
            return Errors.NoAccess("epic").error()
        }
        return epicsRepository.renameEpic(epicId, newName)?.success() ?: Errors.Unknown.error()
    }

    override fun deleteEpic(user: User, epicId: Int): ServiceResult<Unit> {
        if (epicsRepository.getEpicById(epicId) == null) {
            return Errors.NotFound("epic").error()
        }
        val project = epicsRepository.getProjectByEpicId(epicId) ?: return Errors.Unknown.error()
        if (projectsRepository.getUserRightsOnProject(user, project.id) < 0) {
            return Errors.NoAccess("epic").error()
        }
        return if (epicsRepository.deleteEpic(epicId)) Unit.success() else Errors.Unknown.error()
    }
}