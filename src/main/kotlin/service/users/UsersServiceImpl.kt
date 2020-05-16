package service.users

import db.data.User
import repository.users.UsersRepository
import utils.*

class UsersServiceImpl(private val usersRepository: UsersRepository) : UsersService {

    override fun getAllUsers(): ServiceResult<List<User>> {
        return usersRepository.getUsers().success()
    }

    override fun getUserById(id: Int): ServiceResult<User> {
        return usersRepository.findUserById(id)?.success() ?: Errors.NotFound("User").error()
    }

    override fun modifyUser(userId: Int, name: String?, firstName: String?, lastName: String?): ServiceResult<User> {
        if (name != null) {
            val userWithGivenName = usersRepository.findUserByName(name)
            if (userWithGivenName?.id != userId) {
                return Errors.Users.NameAlreadyUsed.error()
            }
        }
        return usersRepository.modifyUser(userId, name, firstName, lastName)?.success() ?: Errors.Unknown.error()
    }

    override fun deleteUser(userId: Int): Boolean {
        return usersRepository.deleteUser(userId)
    }
}