package repository.users

import db.data.UserOutput

interface UsersRepository {

    fun getUsers(): List<UserOutput>

    fun addNewUser(name: String, pass: String): UserOutput?

    fun findUserByNameAndPass(name: String, pass: String): UserOutput?

    fun findUserById(id: Int): UserOutput?
}