package db.dao

import org.jetbrains.exposed.sql.Table

object Projects : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 64)
    val createdAt = long("created_at")

    override val tableName = "projects"

    override val primaryKey = PrimaryKey(id, name = "projects_pk")
}