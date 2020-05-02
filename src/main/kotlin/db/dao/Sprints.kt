package db.dao

import org.jetbrains.exposed.sql.Table

object Sprints : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 32)
    val epicId = integer("epic_id") references Epics.id

    override val tableName = "sprints"

    override val primaryKey = PrimaryKey(id, name = "sprints_pk")
}