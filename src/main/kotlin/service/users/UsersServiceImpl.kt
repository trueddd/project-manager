package service.users

import db.data.User
import db.data.UserUpdateBody
import repository.users.UsersRepository
import utils.*

class UsersServiceImpl(private val usersRepository: UsersRepository) : UsersService {

    override fun getAllUsers(): ServiceResult<List<User>> {
        return usersRepository.getUsers().success()
    }

    override fun getUserById(id: Int): ServiceResult<User> {
        return usersRepository.findUserById(id)?.success() ?: Errors.NotFound("User").error()
    }

    override fun modifyUser(userId: Int, body: UserUpdateBody): ServiceResult<User> {
        if (body.name != null) {
            val userWithGivenName = usersRepository.findUserByName(body.name)
            if (userWithGivenName?.id != userId) {
                return Errors.Users.NameAlreadyUsed.error()
            }
        }
        return usersRepository.modifyUser(userId, body)?.success() ?: Errors.Unknown.error()
    }

    override fun deleteUser(userId: Int): Boolean {
        return usersRepository.deleteUser(userId)
    }
}