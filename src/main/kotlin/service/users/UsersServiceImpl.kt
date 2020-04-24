package service.users

import db.data.UserOutput
import repository.users.UsersRepository

class UsersServiceImpl(private val usersRepository: UsersRepository) : UsersService {

    override fun getAllUsers(): List<UserOutput> {
        return usersRepository.getUsers()
    }

    override fun getUserById(id: Int): UserOutput? {
        return usersRepository.findUserById(id)
    }
}