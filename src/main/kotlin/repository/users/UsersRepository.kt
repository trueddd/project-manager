package repository.users

import db.data.User

interface UsersRepository {

    fun getUsers(): List<User>

    fun findUserById(id: Int): User?

    fun findUserByNameAndPass(name: String, pass: String): User?

    fun findUserByName(name: String): User?

    fun addNewUser(name: String, pass: String): User?

    fun changeTeam(userId: Int, teamId: Int): User?

    fun modifyUser(id: Int, name: String? = null, firstName: String? = null, lastName: String? = null): User?

    fun dropTeam(userId: Int): User?

    fun deleteUser(id: Int): Boolean
}