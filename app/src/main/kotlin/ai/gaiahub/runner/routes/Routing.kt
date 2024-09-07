package ai.gaiahub.runner.routes

import io.ktor.server.application.*
import io.ktor.server.routing.*


fun Application.configureRouting(controller: MainController) {
    routing {
        route("/v1") {
            get("/tools") { controller.listTools(this) }
            get("/runTool/{toolId}") { controller.runTool(this) }
            post("/runCode/{runtime}") { controller.runCode(this) }
            get("/variables/{userId}") { controller.listVariablesByUserId(this) }
        }
    }
}