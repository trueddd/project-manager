package repository.tasks

import db.dao.*
import db.data.tasks.Task
import db.data.tasks.TaskCreateBody
import db.data.tasks.TaskUpdateBody
import org.jetbrains.exposed.sql.*
import repository.BaseRepository
import utils.toTask

class TasksRepositoryImpl(database: Database) : BaseRepository(database), TasksRepository {

    override fun getTasksBySprint(sprintId: Int): List<Task> = query {
        getTasksJoin().select { Tasks.sprintId eq sprintId }.map { it.toTask() }
    }

    override fun getTasksByEpic(epicId: Int): List<Task> = query {
        val sprints = Sprints.select { Sprints.epicId eq epicId }.map { it[Sprints.id].toInt() }
        getTasksJoin().select { Tasks.sprintId inList sprints }.map { it.toTask() }
    }

    override fun getTaskByProject(projectId: Int): List<Task> = query {
        val epics = Epics.select { Epics.projectId eq projectId }.map { it[Epics.id].toInt() }
        val sprints = Sprints.select { Sprints.epicId inList epics }.map { it[Sprints.id].toInt() }
        getTasksJoin().select { Tasks.sprintId inList sprints }.map { it.toTask() }
    }

    override fun getTaskById(taskId: Int): Task? = query {
        return@query getTasksJoin().select { Tasks.id eq taskId }.singleOrNull()?.toTask()
    }

    override fun createTask(userId: Int, body: TaskCreateBody): Task? = query {
        val inserted = Tasks.insert {
            it[name] = body.name
            it[description] = body.description
            it[sprintId] = body.sprintId
            it[createdAt] = System.currentTimeMillis()
            it[creatorId] = userId
            it[stateId] = 1
        }.resultedValues?.firstOrNull()?.let {
            it[Tasks.id].toInt()
        } ?: return@query null
        return@query getTaskById(inserted)
    }

    override fun modifyTask(taskId: Int, updateBody: TaskUpdateBody): Task? = query {
        val updated = Tasks.update({ Tasks.id eq taskId }) {
            updateBody.name?.let { newValue -> it[name] = newValue }
            updateBody.description?.let { newValue -> it[description] = newValue }
            updateBody.stateId?.let { newValue -> it[stateId] = newValue }
            updateBody.sprintId?.let { newValue -> it[sprintId] = newValue }
        }
        if (updated < 1) {
            rollback()
            return@query null
        }
        return@query getTaskById(taskId)
    }

    override fun deleteTask(taskId: Int): Boolean = query {
        return@query Tasks.deleteWhere { Tasks.id eq taskId } > 0
    }

    private fun getTasksJoin() = Tasks leftJoin Sprints leftJoin Users leftJoin TaskStates
}