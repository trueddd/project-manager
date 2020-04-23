import db.UserLoginRequest
import db.UserOutput
import db.Users
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.slf4j.event.Level
import java.math.BigInteger
import java.security.MessageDigest

fun main(args: Array<String>) {
    embeddedServer(Netty, port = getPort(), module = Application::module).start(wait = true)
}

fun getPort() = System.getenv("PORT")?.toInt() ?: 8080

fun Application.module() {

    val database by inject<Database>()

    install(Koin) {
        modules(di.dbModule)
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

//    install(Authentication) {
//        val simpleJWT = SimpleJWT("project-manager-ktor-secret")
//        jwt {
//            verifier(simpleJWT.verifier)
//            validate {
//                UserIdPrincipal(it.payload.getClaim("id").asString())
//            }
//        }
//    }

    install(ContentNegotiation) {
        gson {
        }
    }

    print("starting db: ${database.url}")

    routing {

        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }

        post("/login") {
            val userLogin = call.receiveSafe<UserLoginRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val inDb = transaction(database) {
                return@transaction Users.select { (Users.name eq userLogin.name) and (Users.passHash eq userLogin.pass.hash()) }.singleOrNull()
            }
            if (inDb == null) {
                val user = transaction(database) {
                    Users.insert {
                        it[name] = userLogin.name
                        it[passHash] = userLogin.pass.hash()
                    }.resultedValues?.firstOrNull()?.let {
                        UserOutput(it[Users.id].toInt(), it[Users.name].toString())
                    }
                }
                if (user == null) {
                    call.respond(HttpStatusCode.InternalServerError)
                } else {
                    call.respond(HttpStatusCode.Created, user)
                }
            }
        }
    }
}

suspend inline fun <reified T : Any> ApplicationCall.receiveSafe(): T? {
    return try {
        receive<T>()
    } catch (e: Exception) {
        null
    }
}

fun String.hash(length: Int = 64): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(64, '0')
}