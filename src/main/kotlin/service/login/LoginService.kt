package service.login

import db.data.LoginResponse
import db.data.UserCreateBody
import db.data.UserLoginRequest

interface LoginService {

    fun register(user: UserCreateBody): LoginResponse?

    fun login(userData: UserLoginRequest): LoginResponse?
}