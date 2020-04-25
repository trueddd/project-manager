package db.dao

import org.jetbrains.exposed.sql.Table

object Teams : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 64).uniqueIndex()
    val country = varchar("country", 50).nullable()
    val city = varchar("city", 50).nullable()

    override val primaryKey = PrimaryKey(id, name = "organizations_pk")

    override val tableName = "organizations"
}