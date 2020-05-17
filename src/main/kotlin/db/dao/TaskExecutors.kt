package db.dao

import org.jetbrains.exposed.sql.Table

object TaskExecutors : Table() {
    val taskId = integer("task_id") references Tasks.id
    val executorId = integer("executor_id") references Users.id

    override val tableName = "tasks_executors"
}