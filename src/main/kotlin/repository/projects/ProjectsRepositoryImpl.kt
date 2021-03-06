package repository.projects

import db.dao.*
import db.data.User
import db.data.projects.Project
import db.data.projects.ProjectCreateBody
import db.data.projects.ProjectMember
import db.data.projects.ProjectUpdateBody
import db.data.tasks.TaskWithLogs
import db.data.tasks.Worklog
import org.jetbrains.exposed.sql.*
import repository.BaseRepository
import utils.isOwner
import utils.toExtendedWorklog
import utils.toProject
import utils.toUser
import java.time.LocalDateTime

class ProjectsRepositoryImpl(database: Database) : BaseRepository(database), ProjectsRepository {

    override fun getProjects(): List<Project> = query {
        Projects.selectAll().map { it.toProject() }
    }

    override fun getProjectById(id: Int): Project? = query {
        Projects.select { Projects.id eq id }.singleOrNull()?.toProject()
    }

    override fun createProject(user: User, createBody: ProjectCreateBody): Project? = query {
        val project = Projects.insert {
            it[name] = createBody.name
            it[createdAt] = LocalDateTime.now()
            it[description] = createBody.description
        }.resultedValues?.firstOrNull()?.toProject()
        if (project != null) {
            ProjectsUsers.insert {
                it[projectId] = project.id
                it[userId] = user.id
                it[rights] = 300
            }
        }
        return@query project
    }

    override fun modifyProject(projectId: Int, updateBody: ProjectUpdateBody): Project? = query {
        val modified = Projects.update({ Projects.id eq projectId }) {
            updateBody.name?.let { newValue -> it[name] = newValue }
            updateBody.description?.let { newValue -> it[description] = newValue }
        }
        if (modified < 1) {
            rollback()
            return@query null
        }
        Projects.select { Projects.id eq projectId }.singleOrNull()?.toProject()
    }

    override fun deleteProject(projectId: Int): Boolean = query {
        Projects.deleteWhere { Projects.id eq projectId } > 0
    }

    override fun getUserRightsOnProject(user: User, projectId: Int): Int = query {
        ProjectsUsers
            .select { (ProjectsUsers.projectId eq projectId) and (ProjectsUsers.userId eq user.id) }
            .singleOrNull()?.let { it[ProjectsUsers.rights].toInt() } ?: -1
    }

    override fun getUserRightsOnEpic(user: User, epicId: Int): Int = query {
        val projectId = Epics.select { Epics.id eq epicId }
            .singleOrNull()?.let {
                it[Epics.projectId].toInt()
            } ?: return@query -1
        getUserRightsOnProject(user, projectId)
    }

    override fun getUserRightsOnSprint(user: User, sprintId: Int): Int = query {
        val epicId = Sprints.select { Sprints.id eq sprintId }
            .singleOrNull()?.let {
                it[Sprints.epicId].toInt()
            } ?: return@query -1
        getUserRightsOnEpic(user, epicId)
    }

    override fun getProjectMembers(projectId: Int): List<ProjectMember> = query {
        (ProjectsUsers leftJoin Users)
            .select { ProjectsUsers.projectId eq projectId }
            .map { ProjectMember(it.toUser(), it.isOwner()) }
    }

    override fun addProjectMember(projectId: Int, userId: Int): Boolean = query {
        ProjectsUsers.insert {
            it[ProjectsUsers.projectId] = projectId
            it[ProjectsUsers.userId] = userId
            it[rights] = 0
        }.resultedValues?.firstOrNull() != null
    }

    override fun removeProjectMember(projectId: Int, userId: Int): Boolean = query {
        ProjectsUsers.deleteWhere {
            (ProjectsUsers.projectId eq projectId) and (ProjectsUsers.userId eq userId)
        } > 0
    }

    override fun getProjectWorklogs(projectId: Int): List<TaskWithLogs>? = query {
        (WorkLogs leftJoin Users)
            .join((Tasks leftJoin Sprints leftJoin Epics leftJoin Projects), JoinType.LEFT, WorkLogs.taskId, Tasks.id)
            .select { Projects.id eq projectId }
            .map { it.toExtendedWorklog() }
            .groupBy { it.task }
            .mapValues { v -> v.value.map { Worklog(it.id, it.reporter, it.workStartedAt, it.workFinishedAt, it.comment) } }
            .map { TaskWithLogs(it.key, it.value) }
    }
}