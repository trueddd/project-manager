package db.dao

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.time.LocalDateTime

object Sprints : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 32)
    val epicId = integer("epic_id") references Epics.id
    val start = datetime("start").default(LocalDateTime.now())
    val finish = datetime("finish").default(LocalDateTime.now())

    override val tableName = "sprints"

    override val primaryKey = PrimaryKey(id, name = "sprints_pk")
}