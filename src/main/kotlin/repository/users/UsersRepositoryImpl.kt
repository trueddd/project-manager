package repository.users

import db.data.UserOutput
import db.dao.Users
import org.jetbrains.exposed.sql.*
import repository.BaseRepository
import java.math.BigInteger
import java.security.MessageDigest

class UsersRepositoryImpl(database: Database) : BaseRepository(database), UsersRepository {

    override fun getUsers(): List<UserOutput> {
        return query { Users.selectAll().map { it.toUser() } }
    }

    override fun addNewUser(name: String, pass: String): UserOutput? {
        return query {
            Users.insert {
                it[Users.name] = name
                it[passHash] = pass.hash()
            }.resultedValues?.firstOrNull()?.toUser()
        }
    }

    override fun findUserByNameAndPass(name: String, pass: String): UserOutput? {
        return query {
            Users.select { (Users.name eq name) and (Users.passHash eq pass.hash()) }.singleOrNull()?.toUser()
        }
    }

    override fun findUserById(id: Int): UserOutput? {
        return query {
            Users.select { Users.id eq id }.singleOrNull()?.toUser()
        }
    }

    private fun ResultRow.toUser() = UserOutput(this[Users.id].toInt(), this[Users.name].toString())

    private fun String.hash(length: Int = 64): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(length, '0')
    }
}