import auth.setupJwtAuth
import com.google.gson.*
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.request.path
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.InternalAPI
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.toLocalDateTime
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.get
import org.slf4j.event.Level
import routes.*
import utils.AppEnvironment
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@InternalAPI
@KtorExperimentalAPI
fun main(args: Array<String>) {
    embeddedServer(Netty, port = AppEnvironment.getPort(), module = Application::module).start(wait = true)
}

@InternalAPI
@KtorExperimentalAPI
fun Application.module() {

    install(Koin) {
        modules(di.dbModule, di.repositoryModule, di.serviceModule)
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
        allowCredentials = true
        anyHost()
    }

    install(Authentication) {
        setupJwtAuth(usersService = get())
    }

    install(ContentNegotiation) {
        gson {
            val datePattern = "yyyy-MM-dd'T'HH:mm:ssZ"
            val dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern)
            registerTypeAdapter(LocalDateTime::class.java, object : JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {
                override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime? {
                    return json?.asJsonPrimitive?.asString?.let {
                        SimpleDateFormat(datePattern, Locale.getDefault()).parse(it).toLocalDateTime()
                    }
                }

                override fun serialize(src: LocalDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
                    return ZonedDateTime.of(src, ZoneId.systemDefault())?.format(dateTimeFormatter)?.let { JsonPrimitive(it) }
                }
            })
        }
    }

    routing {

        get("/") {
            call.respond(HttpStatusCode.OK, "Server is up")
        }

        usersRoutes()
        loginRoutes()
        projectsRoutes()
        taskStatesRoutes()
        epicsRoutes()
        sprintsRoutes()
        taskRoutes()
    }
}