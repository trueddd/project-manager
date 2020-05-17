package db.dao

import org.jetbrains.exposed.sql.Table

object WorkLogs : Table() {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id") references Users.id
    val taskId = integer("task_id") references Tasks.id
    val comment = text("comment").nullable()
    val startedAt = long("work_started_at")
    val duration = long("work_duration")

    override val tableName = "work_logs"

    override val primaryKey = PrimaryKey(id, name = "work_logs_pk")
}