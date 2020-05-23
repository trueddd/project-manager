package db.dao

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime

object WorkLogs : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id") references Users.id
    val taskId = integer("task_id") references Tasks.id
    val comment = text("comment").nullable()
    val startedAt = datetime("work_started_at")
    val finishedAt = datetime("work_finished_at")

    override val tableName = "work_logs"

    override val primaryKey = PrimaryKey(id, name = "work_logs_pk")
}