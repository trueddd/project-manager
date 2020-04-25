package service.users

import db.data.User
import repository.users.UsersRepository

class UsersServiceImpl(private val usersRepository: UsersRepository) : UsersService {

    override fun getAllUsers(): List<User> {
        return usersRepository.getUsers()
    }

    override fun getUserById(id: Int): User? {
        return usersRepository.findUserById(id)
    }

    override fun changeTeam(userId: Int, teamId: Int): User? {
        return usersRepository.changeTeam(userId, teamId)
    }
}