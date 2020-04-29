package di

import db.provideDatabase
import org.koin.dsl.binds
import org.koin.dsl.module
import repository.projects.ProjectsRepository
import repository.projects.ProjectsRepositoryImpl
import repository.tasks.states.TaskStatesRepository
import repository.tasks.states.TaskStatesRepositoryImpl
import repository.teams.TeamsRepository
import repository.teams.TeamsRepositoryImpl
import repository.users.UsersRepository
import repository.users.UsersRepositoryImpl
import service.login.LoginService
import service.login.LoginServiceImpl
import service.projects.ProjectsService
import service.projects.ProjectsServiceImpl
import service.tasks.states.TaskStatesService
import service.tasks.states.TaskStatesServiceImpl
import service.teams.TeamsService
import service.teams.TeamsServiceImpl
import service.users.UsersService
import service.users.UsersServiceImpl

val dbModule = module {

    single { provideDatabase() }
}

val repositoryModule = module {

    single { UsersRepositoryImpl(database = get()) } binds arrayOf(UsersRepository::class)

    single<TeamsRepository> { TeamsRepositoryImpl(database = get()) }

    single<ProjectsRepository> { ProjectsRepositoryImpl(database = get()) }

    single<TaskStatesRepository> { TaskStatesRepositoryImpl(database = get()) }
}

val serviceModule = module {

    single<UsersService> { UsersServiceImpl(usersRepository = get()) }

    single<LoginService> { LoginServiceImpl(usersRepository = get()) }

    single<TeamsService> { TeamsServiceImpl(teamsRepository = get(), usersRepository = get()) }

    single<ProjectsService> { ProjectsServiceImpl(projectsRepository = get(), usersRepository = get()) }

    single<TaskStatesService> { TaskStatesServiceImpl(taskStatesRepository = get()) }
}