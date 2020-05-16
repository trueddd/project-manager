package service.users

import db.data.User
import utils.ServiceResult

interface UsersService {

    fun getAllUsers(): ServiceResult<List<User>>

    fun getUserById(id: Int): ServiceResult<User>

    fun modifyUser(userId: Int, name: String? = null, firstName: String? = null, lastName: String? = null): ServiceResult<User>

    fun deleteUser(userId: Int): Boolean
}