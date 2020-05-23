package db.dao

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.time.LocalDateTime

object Projects : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 64)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val description = text("description").nullable()

    override val tableName = "projects"

    override val primaryKey = PrimaryKey(id, name = "projects_pk")
}