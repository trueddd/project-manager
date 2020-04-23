package di

import db.provideDatabase
import org.koin.dsl.module

val dbModule = module {

    single { provideDatabase() }
}