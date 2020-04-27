package service.login

import auth.JwtConfig
import db.data.LoginResponse
import db.data.UserCreateBody
import db.data.UserLoginRequest
import repository.users.UsersRepository

class LoginServiceImpl(private val usersRepository: UsersRepository) : LoginService {

    override fun register(user: UserCreateBody): LoginResponse? {
        val userInDb = usersRepository.findUserByNameAndPass(user.name, user.pass)
        if (userInDb == null) {
            val newUser = usersRepository.addNewUser(user) ?: run {
                println("newuser is null")
                return null
            }
            return LoginResponse(newUser, JwtConfig.makeToken(newUser.id))
        } else {
            return LoginResponse(userInDb, JwtConfig.makeToken(userInDb.id))
        }
    }

    override fun login(userData: UserLoginRequest): LoginResponse? {
        val user = usersRepository.findUserByNameAndPass(userData.name, userData.pass) ?: return null
        val token = JwtConfig.makeToken(user.id)
        return LoginResponse(user, token)
    }
}