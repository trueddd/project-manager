package service.projects

import db.data.User
import db.data.projects.Project
import db.data.projects.ProjectMember
import repository.projects.ProjectsRepository
import repository.users.UsersRepository
import utils.Errors
import utils.ServiceResult
import utils.error
import utils.success

class ProjectsServiceImpl(
    private val projectsRepository: ProjectsRepository,
    private val usersRepository: UsersRepository
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

    override fun getProjectMembers(user: User, projectId: Int): ServiceResult<List<ProjectMember>> {
        if (projectsRepository.getProjectById(projectId) == null) {
            return Errors.NotFound("project").error()
        }
        if (projectsRepository.getUserRightsOnProject(user, projectId) < 0) {
            return Errors.NoAccess("project").error()
        }
        return projectsRepository.getProjectMembers(projectId).success()
    }

    override fun addProjectMember(user: User, projectId: Int, userId: Int): ServiceResult<Unit> {
        if (projectsRepository.getProjectById(projectId) == null) {
            return Errors.NotFound("project").error()
        }
        if (usersRepository.findUserById(userId) == null) {
            return Errors.NotFound("user").error()
        }
        if (projectsRepository.getUserRightsOnProject(user, projectId) < 0) {
            return Errors.NoAccess("project").error()
        }
        return if (projectsRepository.addProjectMember(projectId, userId)) Unit.success() else Errors.Unknown.error()
    }

    override fun removeProjectMember(user: User, projectId: Int, userId: Int): ServiceResult<Unit> {
        if (projectsRepository.getProjectById(projectId) == null) {
            return Errors.NotFound("project").error()
        }
        if (usersRepository.findUserById(userId) == null) {
            return Errors.NotFound("user").error()
        }
        if (projectsRepository.getUserRightsOnProject(user, projectId) < 0) {
            return Errors.NoAccess("project").error()
        }
        val deleted = projectsRepository.removeProjectMember(projectId, userId)
        if (deleted) {
            if (projectsRepository.getProjectMembers(projectId).none { it.isOwner }) {
                projectsRepository.deleteProject(projectId)
            }
        }
        return if (deleted) Unit.success() else Errors.Unknown.error()
    }
}