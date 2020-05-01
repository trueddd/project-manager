package repository.projects

import db.data.User
import db.data.projects.Project

interface ProjectsRepository {

    fun getTeamProjects(teamId: Int): List<Project>

    fun getProjectById(id: Int): Project?

    fun createProject(teamId: Int, name: String): Project?

    fun modifyProject(projectId: Int, name: String): Project?

    fun deleteProject(projectId: Int): Boolean

    fun isProjectFromTeam(projectId: Int, teamId: Int): Boolean

    fun isUserRelatedToProject(user: User, projectId: Int): Boolean
}