package service.projects

import db.data.User
import db.data.projects.Project
import db.data.projects.ProjectCreateBody
import db.data.projects.ProjectMember
import db.data.projects.ProjectUpdateBody
import utils.ServiceResult

interface ProjectsService {

    fun getTeamProjects(user: User): ServiceResult<List<Project>>

    fun createProject(user: User, createBody: ProjectCreateBody): ServiceResult<Project>

    fun modifyProject(user: User, projectId: Int, updateBody: ProjectUpdateBody): ServiceResult<Project>

    fun deleteProject(user: User, projectId: Int): ServiceResult<Unit>

    fun getProjectMembers(user: User, projectId: Int): ServiceResult<List<ProjectMember>>

    fun addProjectMember(user: User, projectId: Int, userId: Int): ServiceResult<Unit>

    fun removeProjectMember(user: User, projectId: Int, userId: Int): ServiceResult<Unit>
}