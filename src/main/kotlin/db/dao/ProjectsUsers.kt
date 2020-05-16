package db.dao

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object ProjectsUsers : Table() {
    val projectId = integer("project_id") references Projects.id
    val userId = integer("user_id") references Users.id
    val rights = integer("rights").default(0)

    override val tableName = "projects_users"

    fun ResultRow.isOwner() = this[rights].toInt() >= 300
}