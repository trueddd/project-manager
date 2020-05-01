package db.dao

import org.jetbrains.exposed.sql.Table

object Epics : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 32)
    val projectId = integer("project_id") references Projects.id

    override val tableName = "epics"

    override val primaryKey = PrimaryKey(id, name = "epics_pk")
}