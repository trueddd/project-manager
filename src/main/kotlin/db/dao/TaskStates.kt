package db.dao

import org.jetbrains.exposed.sql.Table

object TaskStates : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 32)

    override val tableName = "task_states"

    override val primaryKey = PrimaryKey(id, name = "task_states_pk")
}