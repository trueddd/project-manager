package service.projects

import db.data.User
import db.data.projects.Project
import repository.projects.ProjectsRepository
import utils.Errors
import utils.ServiceResult
import utils.error
import utils.success

class ProjectsServiceImpl(
    private val projectsRepository: ProjectsRepository
) : ProjectsService {

    override fun getTeamProjects(user: User): ServiceResult<List<Project>> {
        return projectsRepository.getProjects().success()
    }

    override fun createProject(user: User, projectName: String): ServiceResult<Project> {
        val project = projectsRepository.createProject(user, projectName)
        return project?.success() ?: Errors.Unknown.error()
    }

    override fun modifyProject(user: User, projectId: Int, newName: String): ServiceResult<Project> {
        if (projectsRepository.getProjectById(projectId) == null) {
            return Errors.NotFound("project").error()
        }
        if (projectsRepository.getUserRightsOnProject(user, projectId) < 0) {
            return Errors.NoAccess("project").error()
        }
        val project = projectsRepository.modifyProject(projectId, newName)
        return project?.success() ?: Errors.Unknown.error()
    }

    override fun deleteProject(user: User, projectId: Int): ServiceResult<Unit> {
        if (projectsRepository.getProjectById(projectId) == null) {
            return Errors.NotFound("project").error()
        }
        if (projectsRepository.getUserRightsOnProject(user, projectId) < 0) {
            return Errors.NoAccess("project").error()
        }
        val deleted = projectsRepository.deleteProject(projectId)
        return if (deleted) Unit.success() else Errors.Unknown.error()
    }
}