import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    embeddedServer(Netty, port = getPort(), module = Application::module).start(wait = true)
}

fun getPort() = System.getenv("PORT")?.toInt() ?: 8080