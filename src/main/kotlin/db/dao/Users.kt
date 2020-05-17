package db.dao

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 20)
    val firstName = varchar("first_name", 20).nullable()
    val lastName = varchar("last_name", 20).nullable()
    val passHash = varchar("pass_hash", 64)
    val phone = varchar("phone", 15).nullable()
    val email = varchar("email", 32).nullable()
    val teamStatus = varchar("team_status", 32).nullable()

    override val primaryKey = PrimaryKey(id, name = "users_pk")

    override val tableName = "users"
}