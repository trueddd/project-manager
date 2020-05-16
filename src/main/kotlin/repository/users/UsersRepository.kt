package repository.users

import db.data.User
import db.data.UserCreateBody

interface UsersRepository {

    fun getUsers(): List<User>

    fun findUserById(id: Int): User?

    fun findUserByNameAndPass(name: String, pass: String): User?

    fun findUserByName(name: String): User?

    fun addNewUser(user: UserCreateBody): User?

    fun modifyUser(id: Int, name: String? = null, firstName: String? = null, lastName: String? = null): User?

    fun deleteUser(id: Int): Boolean
}