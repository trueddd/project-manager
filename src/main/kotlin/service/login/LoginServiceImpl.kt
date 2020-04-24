package service.login

import auth.JwtConfig
import db.data.LoginResponse
import db.data.UserLoginRequest
import repository.users.UsersRepository

class LoginServiceImpl(private val usersRepository: UsersRepository) : LoginService {

    override fun loginUser(userLogin: UserLoginRequest): LoginResponse? {
        val userInDb = usersRepository.findUserByNameAndPass(userLogin.name, userLogin.pass)
        if (userInDb == null) {
            val newUser = usersRepository.addNewUser(userLogin.name, userLogin.pass) ?: return null
            return LoginResponse(newUser, JwtConfig.makeToken(newUser.id))
        } else {
            return LoginResponse(userInDb, JwtConfig.makeToken(userInDb.id))
        }
    }

    override fun getNewToken(userData: UserLoginRequest): LoginResponse? {
        val user = usersRepository.findUserByNameAndPass(userData.name, userData.pass) ?: return null
        val token = JwtConfig.makeToken(user.id)
        return LoginResponse(user, token)
    }
}