package di

import db.provideDatabase
import org.koin.dsl.module
import repository.users.UsersRepository
import repository.users.UsersRepositoryImpl
import service.login.LoginService
import service.login.LoginServiceImpl
import service.users.UsersService
import service.users.UsersServiceImpl

val dbModule = module {

    single { provideDatabase() }
}

val repositoryModule = module {

    single<UsersRepository> { UsersRepositoryImpl(database = get()) }
}

val serviceModule = module {

    single<UsersService> { UsersServiceImpl(usersRepository = get()) }

    single<LoginService> { LoginServiceImpl(usersRepository = get()) }
}