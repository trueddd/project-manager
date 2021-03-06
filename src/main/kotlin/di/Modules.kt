package di

import db.provideDatabase
import org.koin.dsl.binds
import org.koin.dsl.module
import repository.projects.ProjectsRepository
import repository.projects.ProjectsRepositoryImpl
import repository.tasks.TasksRepository
import repository.tasks.TasksRepositoryImpl
import repository.tasks.epics.EpicsRepository
import repository.tasks.epics.EpicsRepositoryImpl
import repository.tasks.sprints.SprintsRepository
import repository.tasks.sprints.SprintsRepositoryImpl
import repository.tasks.states.TaskStatesRepository
import repository.tasks.states.TaskStatesRepositoryImpl
import repository.users.UsersRepository
import repository.users.UsersRepositoryImpl
import service.login.LoginService
import service.login.LoginServiceImpl
import service.projects.ProjectsService
import service.projects.ProjectsServiceImpl
import service.tasks.TasksService
import service.tasks.TasksServiceImpl
import service.tasks.sprints.SprintsService
import service.tasks.sprints.SprintsServiceImpl
import service.tasks.epics.EpicsService
import service.tasks.epics.EpicsServiceImpl
import service.tasks.states.TaskStatesService
import service.tasks.states.TaskStatesServiceImpl
import service.users.UsersService
import service.users.UsersServiceImpl

val dbModule = module {

    single { provideDatabase() }
}

val repositoryModule = module {

    single { UsersRepositoryImpl(database = get()) } binds arrayOf(UsersRepository::class)

    single<ProjectsRepository> { ProjectsRepositoryImpl(database = get()) }

    single<TaskStatesRepository> { TaskStatesRepositoryImpl(database = get()) }

    single<EpicsRepository> { EpicsRepositoryImpl(database = get()) }

    single<SprintsRepository> { SprintsRepositoryImpl(database = get()) }

    single<TasksRepository> { TasksRepositoryImpl(database = get()) }
}

val serviceModule = module {

    single<UsersService> { UsersServiceImpl(usersRepository = get()) }

    single<LoginService> { LoginServiceImpl(usersRepository = get()) }

    single<ProjectsService> { ProjectsServiceImpl(projectsRepository = get(), usersRepository = get()) }

    single<TaskStatesService> { TaskStatesServiceImpl(taskStatesRepository = get()) }

    single<EpicsService> { EpicsServiceImpl(epicsRepository = get(), projectsRepository = get()) }

    single<SprintsService> { SprintsServiceImpl(sprintsRepository = get(), epicsRepository = get(), projectsRepository = get()) }

    single<TasksService> {
        TasksServiceImpl(
            projectsRepository = get(),
            epicsRepository = get(),
            sprintsRepository = get(),
            tasksRepository = get())
    }
}