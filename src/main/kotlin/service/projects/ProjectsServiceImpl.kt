package service.projects

import db.data.User
import db.data.projects.Project
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

    override fun getTeamProjects(teamId: Int, user: User): ServiceResult<List<Project>> {
        if (!usersRepository.isUserFromTeam(user.id, teamId)) {
            return Errors.NoAccess("team").error()
        }
        return projectsRepository.getTeamProjects(teamId).success()
    }

    override fun createProject(user: User, projectName: String): ServiceResult<Project> {
        if (user.team == null) {
            return Errors.NotFound("Team").error()
        }
        val project = projectsRepository.createProject(user.team.id, projectName)
        return project?.success() ?: Errors.Unknown.error()
    }

    override fun modifyProject(user: User, projectId: Int, newName: String): ServiceResult<Project> {
        if (user.team == null) {
            return Errors.NoAccess("project").error()
        }
        if (!projectsRepository.isProjectFromTeam(projectId, user.team.id)) {
            return Errors.NoAccess("project").error()
        }
        val project = projectsRepository.modifyProject(projectId, newName)
        return project?.success() ?: Errors.Unknown.error()
    }

    override fun deleteProject(user: User, projectId: Int): ServiceResult<Unit> {
        if (user.team == null) {
            return Errors.NoAccess("project").error()
        }
        if (!projectsRepository.isProjectFromTeam(projectId, user.team.id)) {
            return Errors.NoAccess("project").error()
        }
        val deleted = projectsRepository.deleteProject(projectId)
        return if (deleted) Unit.success() else Errors.Unknown.error()
    }
}