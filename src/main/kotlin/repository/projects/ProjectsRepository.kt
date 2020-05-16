package repository.projects

import db.data.User
import db.data.projects.Project

interface ProjectsRepository {

    fun getProjects(): List<Project>

    fun getProjectById(id: Int): Project?

    fun createProject(user: User, name: String): Project?

    fun modifyProject(projectId: Int, name: String): Project?

    fun deleteProject(projectId: Int): Boolean

    fun getUserRightsOnProject(user: User, projectId: Int): Int
}