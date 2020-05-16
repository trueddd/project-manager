package repository.projects

import db.dao.*
import db.data.User
import db.data.projects.Project
import db.data.projects.ProjectMember
import org.jetbrains.exposed.sql.*
import repository.BaseRepository
import utils.isOwner
import utils.toProject
import utils.toUser

class ProjectsRepositoryImpl(database: Database) : BaseRepository(database), ProjectsRepository {

    override fun getProjects(): List<Project> = query {
        Projects.selectAll().map { it.toProject() }
    }

    override fun getProjectById(id: Int): Project? = query {
        return@query Projects.select { Projects.id eq id }.singleOrNull()?.toProject()
    }

    override fun createProject(user: User, name: String): Project? = query {
        val project = Projects.insert {
            it[Projects.name] = name
            it[createdAt] = System.currentTimeMillis()
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

    override fun modifyProject(projectId: Int, name: String): Project? = query {
        val modified = Projects.update({ Projects.id eq projectId }) {
            it[Projects.name] = name
        }
        if (modified < 1) {
            rollback()
            return@query null
        }
        return@query Projects.select { Projects.id eq projectId }.singleOrNull()?.toProject()
    }

    override fun deleteProject(projectId: Int): Boolean = query {
        Projects.deleteWhere { Projects.id eq projectId } > 0
    }

    override fun getUserRightsOnProject(user: User, projectId: Int): Int = query {
        return@query ProjectsUsers
            .select { (ProjectsUsers.projectId eq projectId) and (ProjectsUsers.userId eq user.id) }
            .singleOrNull()?.let { it[ProjectsUsers.rights].toInt() } ?: -1
    }

    override fun getUserRightsOnEpic(user: User, epicId: Int): Int = query {
        val projectId = Epics.select { Epics.id eq epicId }
            .singleOrNull()?.let {
                it[Epics.projectId].toInt()
            } ?: return@query -1
        return@query getUserRightsOnProject(user, projectId)
    }

    override fun getUserRightsOnSprint(user: User, sprintId: Int): Int = query {
        val epicId = Sprints.select { Sprints.id eq sprintId }
            .singleOrNull()?.let {
                it[Sprints.epicId].toInt()
            } ?: return@query -1
        return@query getUserRightsOnEpic(user, epicId)
    }

    override fun getUserRightsOnTask(user: User, taskId: Int): Int = query {
        val sprintId = Tasks.select { Tasks.id eq taskId }
            .singleOrNull()?.let {
                it[Tasks.sprintId].toInt()
            } ?: return@query -1
        return@query getUserRightsOnSprint(user, sprintId)
    }

    override fun getProjectMembers(projectId: Int): List<ProjectMember> = query {
        return@query (ProjectsUsers leftJoin Users)
            .select { ProjectsUsers.projectId eq projectId }
            .map { ProjectMember(it.toUser(), it.isOwner()) }
    }

    override fun addProjectMember(projectId: Int, userId: Int): Boolean = query {
        return@query ProjectsUsers.insert {
            it[ProjectsUsers.projectId] = projectId
            it[ProjectsUsers.userId] = userId
            it[rights] = 0
        }.resultedValues?.firstOrNull() != null
    }

    override fun removeProjectMember(projectId: Int, userId: Int): Boolean = query {
        return@query ProjectsUsers.deleteWhere {
            (ProjectsUsers.projectId eq projectId) and (ProjectsUsers.userId eq userId)
        } > 0
    }
}