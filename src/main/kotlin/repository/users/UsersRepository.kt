package repository.users

import db.data.User
import db.data.UserCreateBody
import db.data.UserUpdateBody

interface UsersRepository {

    fun getUsers(): List<User>

    fun findUserById(id: Int): User?

    fun findUserByNameAndPass(name: String, pass: String): User?

    fun findUserByName(name: String): User?

    fun addNewUser(user: UserCreateBody): User?

    fun modifyUser(id: Int, body: UserUpdateBody): User?

    fun deleteUser(id: Int): Boolean
}