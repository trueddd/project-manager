package repository.tasks.states

import db.dao.TaskStates
import db.data.tasks.TaskState
import org.jetbrains.exposed.sql.*
import repository.BaseRepository

class TaskStatesRepositoryImpl(database: Database) : BaseRepository(database), TaskStatesRepository {

    override fun getTaskStates(): List<TaskState> = query {
        TaskStates
            .selectAll()
            .map { it.toState() }
    }

    override fun getTaskStateById(id: Int): TaskState? = query {
        return@query TaskStates.select { TaskStates.id eq id }.singleOrNull()?.toState()
    }

    override fun createTaskState(name: String): TaskState? = query {
        return@query TaskStates.insert {
            it[TaskStates.name] = name
        }.resultedValues?.firstOrNull()?.toState()
    }

    override fun renameTaskState(id: Int, name: String): TaskState? = query {
        val affected = TaskStates.update({ TaskStates.id eq id }) {
            it[TaskStates.name] = name
        }
        if (affected <= 0) {
            rollback()
            return@query null
        }
        return@query TaskStates.select { TaskStates.id eq id }.singleOrNull()?.toState()
    }

    override fun deleteTaskState(id: Int): Boolean = query {
        TaskStates.deleteWhere { TaskStates.id eq id } > 0
    }

    private fun ResultRow.toState(): TaskState {
        return TaskState(
            this[TaskStates.id].toInt(),
            this[TaskStates.name].toString()
        )
    }
}