package repository.projects

import db.data.User
import db.data.projects.Project
import db.data.projects.ProjectCreateBody
import db.data.projects.ProjectMember
import db.data.projects.ProjectUpdateBody
import db.data.tasks.TaskWithLogs

interface ProjectsRepository {

    fun getProjects(): List<Project>

    fun getProjectById(id: Int): Project?

    fun createProject(user: User, createBody: ProjectCreateBody): Project?

    fun modifyProject(projectId: Int, updateBody: ProjectUpdateBody): Project?

    fun deleteProject(projectId: Int): Boolean

    fun getUserRightsOnProject(user: User, projectId: Int): Int

    fun getUserRightsOnEpic(user: User, epicId: Int): Int

    fun getUserRightsOnSprint(user: User, sprintId: Int): Int

    fun getProjectMembers(projectId: Int): List<ProjectMember>

    fun addProjectMember(projectId: Int, userId: Int): Boolean

    fun removeProjectMember(projectId: Int, userId: Int): Boolean

    fun getProjectWorklogs(projectId: Int): List<TaskWithLogs>?
}