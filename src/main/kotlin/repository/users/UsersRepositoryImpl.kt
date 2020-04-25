package repository.users

import db.dao.Teams
import db.data.User
import db.dao.Users
import db.data.Team
import org.jetbrains.exposed.sql.*
import repository.BaseRepository
import java.math.BigInteger
import java.security.MessageDigest

class UsersRepositoryImpl(database: Database) : BaseRepository(database), UsersRepository {

    override fun getUsers(): List<User> {
        return query { (Users leftJoin Teams).selectAll().map { it.toUser() } }
    }

    override fun addNewUser(name: String, pass: String): User? {
        return query {
            Users.insert {
                it[Users.name] = name
                it[passHash] = pass.hash()
            }
            (Users leftJoin Teams)
                .select { Users.name eq name }.singleOrNull()?.toUser()
        }
    }

    override fun findUserByNameAndPass(name: String, pass: String): User? {
        return query {
            (Users leftJoin Teams)
                .select { (Users.name eq name) and (Users.passHash eq pass.hash()) }
                .singleOrNull()?.toUser()
        }
    }

    override fun findUserByName(name: String): User? {
        return query {
            Users.select { Users.name eq name }.singleOrNull()?.toUser(withTeam = false)
        }
    }

    override fun findUserById(id: Int): User? {
        return query {
            (Users leftJoin Teams)
                .select { Users.id eq id }.singleOrNull()?.toUser()
        }
    }

    override fun changeTeam(userId: Int, teamId: Int): User? {
        return query {
            val affected = Users.update({ Users.id eq userId }) {
                it[this.teamId] = teamId
            }
            if (affected <= 0) {
                return@query null
            }
            (Users leftJoin Teams)
                .select { Users.id eq userId }.singleOrNull()?.toUser()
        }
    }

    override fun modifyUser(id: Int, name: String?, firstName: String?, lastName: String?): User? {
        return query {
            val affected = Users.update({ Users.id eq id }) {
                name?.let { newName -> it[this.name] = newName }
                firstName?.let { newFirst -> it[this.firstName] = newFirst }
                lastName?.let { newLast -> it[this.lastName] = newLast }
            }
            if (affected <= 0) {
                return@query null
            }
            (Users leftJoin Teams)
                .select { Users.id eq id }.singleOrNull()?.toUser()
        }
    }

    override fun dropTeam(userId: Int): User? {
        return query {
            val affected = Users.update({ Users.id eq userId }) {
                it[teamId] = null
            }
            if (affected <= 0) {
                return@query null
            }
            (Users leftJoin Teams)
                .select { Users.id eq userId }.singleOrNull()?.toUser()
        }
    }

    override fun deleteUser(id: Int): Boolean {
        return query {
            Users.deleteWhere { Users.id eq id } > 0
        }
    }

    private fun ResultRow.toUser(withTeam: Boolean = true): User {
        return User(
            this[Users.id].toInt(),
            this[Users.name].toString(),
            this[Users.firstName]?.toString(),
            this[Users.lastName]?.toString(),
            if (withTeam && this[Users.teamId] != null) {
                Team(
                    this[Teams.id].toInt(),
                    this[Teams.name].toString(),
                    this[Teams.country]?.toString(),
                    this[Teams.city]?.toString()
                )
            } else null
        )
    }

    private fun String.hash(length: Int = 64): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(length, '0')
    }
}