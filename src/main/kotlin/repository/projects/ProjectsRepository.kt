package repository.projects

import db.data.User
import db.data.projects.Project
import db.data.projects.ProjectMember

interface ProjectsRepository {

    fun getProjects(): List<Project>

    fun getProjectById(id: Int): Project?

    fun createProject(user: User, name: String): Project?

    fun modifyProject(projectId: Int, name: String): Project?

    fun deleteProject(projectId: Int): Boolean

    fun getUserRightsOnProject(user: User, projectId: Int): Int

    fun getUserRightsOnEpic(user: User, epicId: Int): Int

    fun getUserRightsOnSprint(user: User, sprintId: Int): Int

    fun getProjectMembers(projectId: Int): List<ProjectMember>

    fun addProjectMember(projectId: Int, userId: Int): Boolean

    fun removeProjectMember(projectId: Int, userId: Int): Boolean
}