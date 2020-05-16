package service.projects

import db.data.User
import db.data.projects.Project
import db.data.projects.ProjectMember
import utils.ServiceResult

interface ProjectsService {

    fun getTeamProjects(user: User): ServiceResult<List<Project>>

    fun createProject(user: User, projectName: String): ServiceResult<Project>

    fun modifyProject(user: User, projectId: Int, newName: String): ServiceResult<Project>

    fun deleteProject(user: User, projectId: Int): ServiceResult<Unit>

    fun getProjectMembers(user: User, projectId: Int): ServiceResult<List<ProjectMember>>

    fun addProjectMember(user: User, projectId: Int, userId: Int): ServiceResult<Unit>

    fun removeProjectMember(user: User, projectId: Int, userId: Int): ServiceResult<Unit>
}