package service.login

import db.data.LoginResponse
import db.data.UserCreateBody
import db.data.UserLoginRequest
import utils.ServiceResult

interface LoginService {

    fun register(user: UserCreateBody): ServiceResult<LoginResponse>

    fun login(userData: UserLoginRequest): ServiceResult<LoginResponse>
}