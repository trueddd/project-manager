package repository.projects

import db.dao.Projects
import db.dao.ProjectsUsers
import db.data.User
import db.data.projects.Project
import org.jetbrains.exposed.sql.*
import repository.BaseRepository
import utils.toProject

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
}