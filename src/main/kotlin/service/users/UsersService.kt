package service.users

import db.data.User
import db.data.UserUpdateBody
import utils.ServiceResult

interface UsersService {

    fun getAllUsers(): ServiceResult<List<User>>

    fun getUserById(id: Int): ServiceResult<User>

    fun modifyUser(userId: Int, body: UserUpdateBody): ServiceResult<User>

    fun deleteUser(userId: Int): Boolean
}