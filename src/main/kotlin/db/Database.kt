package db

import com.sun.org.apache.xpath.internal.operations.Or
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.net.URI

fun provideDatabase(): Database {
//    val url = System.getenv("DATABASE_URL")
//    val uri = URI(url)
//    val username = uri.userInfo.split(":")[0]
//    val password = uri.userInfo.split(":")[1]
//    val dbUrl = "jdbc:postgresql://${uri.host}:${uri.port}${uri.path}?sslmode=require"
//    return Database.connect(dbUrl, "org.postgresql.Driver", username, password).apply {
//        SchemaUtils.create(Organizations, Users)
//    }

    return Database.connect("jdbc:postgresql://ec2-54-247-94-127.eu-west-1.compute.amazonaws.com:5432/d90fjfvot5r5o5?sslmode=require", "org.postgresql.Driver", "sntmyzdtdyisgt", "d8ca5b0ae1a293fed56cf609acf098cb04722b69b14f4006fbc3feb1d8455e91").apply {
        transaction {
            SchemaUtils.create(Organizations, Users)
        }
    }
}