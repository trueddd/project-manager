package service.login

import auth.JwtConfig
import db.data.LoginResponse
import db.data.UserCreateBody
import db.data.UserLoginRequest
import repository.users.UsersRepository
import utils.Errors
import utils.ServiceResult
import utils.error
import utils.success

class LoginServiceImpl(private val usersRepository: UsersRepository) : LoginService {

    override fun register(user: UserCreateBody): ServiceResult<LoginResponse> {
        if (usersRepository.findUserByName(user.name) != null) {
            return Errors.Conflict("user").error()
        }
        val newUser = usersRepository.addNewUser(user) ?: return Errors.Unknown.error()
        return LoginResponse(newUser, JwtConfig.makeToken(newUser.id)).success()
    }

    override fun login(userData: UserLoginRequest): ServiceResult<LoginResponse> {
        if (usersRepository.findUserByName(userData.name) == null) {
            return Errors.NotFound("user").error()
        }
        val user = usersRepository.findUserByNameAndPass(userData.name, userData.pass) ?: return Errors.WrongPass.error()
        val token = JwtConfig.makeToken(user.id)
        return LoginResponse(user, token).success()
    }
}