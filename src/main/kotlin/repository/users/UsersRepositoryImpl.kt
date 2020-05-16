package repository.users

import db.data.User
import db.dao.Users
import db.data.UserCreateBody
import org.jetbrains.exposed.sql.*
import repository.BaseRepository
import utils.toUser
import java.math.BigInteger
import java.security.MessageDigest

class UsersRepositoryImpl(database: Database) : BaseRepository(database), UsersRepository {

    override fun getUsers(): List<User> {
        return query { Users.selectAll().map { it.toUser() } }
    }

    override fun addNewUser(user: UserCreateBody): User? {
        return query {
            Users.insert {
                it[name] = user.name
                it[passHash] = user.pass.hash()
                it[firstName] = user.firstName
                it[lastName] = user.lastName
            }
            Users.select { Users.name eq user.name }.singleOrNull()?.toUser()
        }
    }

    override fun findUserByNameAndPass(name: String, pass: String): User? {
        return query {
            Users
                .select { (Users.name eq name) and (Users.passHash eq pass.hash()) }
                .singleOrNull()?.toUser()
        }
    }

    override fun findUserByName(name: String): User? {
        return query {
            Users.select { Users.name eq name }.singleOrNull()?.toUser()
        }
    }

    override fun findUserById(id: Int): User? {
        return query {
            Users.select { Users.id eq id }.singleOrNull()?.toUser()
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
            Users.select { Users.id eq id }.singleOrNull()?.toUser()
        }
    }

    override fun deleteUser(id: Int): Boolean {
        return query {
            Users.deleteWhere { Users.id eq id } > 0
        }
    }

    private fun String.hash(length: Int = 64): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(length, '0')
    }
}