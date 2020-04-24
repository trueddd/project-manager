package service.users

import db.data.UserOutput

interface UsersService {

    fun getAllUsers(): List<UserOutput>

    fun getUserById(id: Int): UserOutput?
}