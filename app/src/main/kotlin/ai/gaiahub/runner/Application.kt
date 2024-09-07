package ai.gaiahub.runner

import ai.gaiahub.runner.data.repositories.implementations.DatabaseConnection
import ai.gaiahub.runner.data.repositories.implementations.connectToDatabase
import ai.gaiahub.runner.domain.services.tools.ToolsService
import ai.gaiahub.runner.domain.services.variables.VariablesService
import ai.gaiahub.runner.routes.MainController
import ai.gaiahub.runner.routes.configureRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.ktorm.database.Database

const val ARG_PORT = "--port"
const val ARG_JDBC_CONNECTION_STRING = "--jdbc"


class Server: KoinComponent {
     fun start(host: String, port: Int, jdbcConnectionString: String) {

         // Start Dependency Injection service
         startKoin {
             modules(sharedModule)
         }

         // Connect to database
         val dataBase: Database = connectToDatabase(jdbcConnectionString)
         val databaseConnection: DatabaseConnection by inject()
         databaseConnection.database = dataBase

         // Start server
         val toolsService by inject<ToolsService>()
         val variableService by inject<VariablesService>()
         val injectedController = MainController(
             toolsService = toolsService,
             variablesService = variableService
         )

         embeddedServer(
             Netty,
             port = port,
             host = host,
             module = {
                    gaiaModule(controller = injectedController)
             }
         ).start(wait = true)
     }
}
private fun Application.gaiaModule(controller: MainController) {
    configureRouting(controller = controller)
}

fun main(args: Array<String>) {
    println("Starting server with args: ${args.joinToString()}")
    val argsMap = args.asMap()
    val port = argsMap.getOrElse(ARG_PORT){ "8020" }.toInt()
    val jdbcConnectionString = argsMap.getOrElse(ARG_JDBC_CONNECTION_STRING){ "0.0.0.0" }.toString()

    Server().start(
        host = "0.0.0.0",
        port = port,
        jdbcConnectionString = jdbcConnectionString
    )
}
