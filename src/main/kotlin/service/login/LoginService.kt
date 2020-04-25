package service.login

import db.data.LoginResponse
import db.data.UserLoginRequest

interface LoginService {

    fun loginUser(userLogin: UserLoginRequest): LoginResponse?

    fun getNewToken(userData: UserLoginRequest): LoginResponse?
}