package repository.users

import db.data.User

interface UsersRepository {

    fun getUsers(): List<User>

    fun addNewUser(name: String, pass: String): User?

    fun findUserByNameAndPass(name: String, pass: String): User?

    fun findUserById(id: Int): User?

    fun changeTeam(userId: Int, teamId: Int): User?
}