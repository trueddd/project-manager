package service.users

import db.data.User

interface UsersService {

    fun getAllUsers(): List<User>

    fun getUserById(id: Int): User?

    fun changeTeam(userId: Int, teamId: Int): User?
}