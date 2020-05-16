package db.dao

import org.jetbrains.exposed.sql.Table

object Tasks : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 64)
    val description = text("description").nullable()
    val creatorId = integer("creator_id") references Users.id
    val createdAt = long("created_at")
    val stateId = (integer("state") references TaskStates.id).default(1)
    val sprintId = integer("sprint_id") references Sprints.id

    override val tableName = "tasks"

    override val primaryKey = PrimaryKey(id, name = "tasks_pk")
}