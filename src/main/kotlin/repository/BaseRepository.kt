package repository

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.transaction

abstract class BaseRepository(private val database: Database) {

    protected fun <T> query(statement: Transaction.() -> T): T {
        return transaction(database, statement)
    }
}