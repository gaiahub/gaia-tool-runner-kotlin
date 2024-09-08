package ai.gaiahub.runner.routes

import ai.gaiahub.runner.domain.models.GaiaRuntime
import ai.gaiahub.runner.domain.models.RunnableProgram
import ai.gaiahub.runner.domain.models.SandboxExecutionResult
import ai.gaiahub.runner.domain.services.runtime.LINUX
import ai.gaiahub.runner.domain.services.runtime.NODE_JS
import ai.gaiahub.runner.domain.services.sandbox.SandboxService
import ai.gaiahub.runner.domain.services.tools.ToolsService
import ai.gaiahub.runner.domain.services.variables.VariablesService
import ai.gaiahub.runner.routes.exceptions.StartingCommandNotFound
import ai.gaiahub.runner.routes.exceptions.UnsupportedRuntime
import ai.gaiahub.runner.routes.models.RunCodeRequest
import ai.gaiahub.runner.routes.models.RunCodeResult
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class MainController(private val toolsService: ToolsService, private val variablesService: VariablesService) {

    internal suspend fun listVariablesByUserId(pipelineContext: PipelineContext<Unit, ApplicationCall>) {
        val call = pipelineContext.call
        val userID = call.parameters["userId"].toString()
        val response = variablesService.getVariablesByUserId(userID)

        response.onSuccess {
            call.respondText(response.getOrNull()?.joinToString { variable -> "Variable: $variable" } ?: "No variables found")
        }
        response.onFailure {
            call.respondText("Error: ${response.exceptionOrNull()?.message}")
        }
    }

    internal suspend fun listTools(pipelineContext: PipelineContext<Unit, ApplicationCall>) {
        val call = pipelineContext.call
        val response = toolsService.listAvailableTools()

        response.onSuccess {
            call.respondText(response.getOrNull()?.joinToString { tool -> "Tool ID: $tool" } ?: "No tools found")
        }
        response.onFailure {
            call.respondText("Error: ${response.exceptionOrNull()?.message}")
        }
    }

    internal suspend fun runTool(pipelineContext: PipelineContext<Unit, ApplicationCall>) {
        val call = pipelineContext.call
        val toolId = call.parameters["toolId"]

        if (toolId == null) {
            call.respondText("Tool ID not found")
            return
        }

        val response = runToolSourceCode(toolId)

        response.onSuccess {
            call.respondText(response.getOrNull() ?: "No source code found")
        }
        response.onFailure {
            call.respondText("Error: ${response.exceptionOrNull()?.message}")
        }
    }

    private fun runToolSourceCode(toolId: String): Result<String> {
        val sourceCode = toolsService.getToolSourceCode(toolId)

        val runCodeRequest = RunCodeRequest(
            runtime = "node",
            data = sourceCode.getOrNull()?.toByteArray() ?: ByteArray(0)
        )

        val runnableProgramResult = createRunnableProgram(runCodeRequest)

        // If the runnable program creation succeeded, run the program in the sandbox
        runnableProgramResult.onSuccess {

            // Run the program in the sandbox
            SandboxService.runRunnableProgramInSandbox(it)
                .onSuccess {
                    return Result.success("Program output: ${it.toCodeResultJSON()}")
                }.onFailure {
                    return Result.failure(Exception("Error: $it"))
                }

        }.onFailure {
            return Result.failure(Exception("Error: ${it.message}"))
        }
        return sourceCode
    }
    internal suspend fun runCode(pipelineContext: PipelineContext<Unit, ApplicationCall>) {
        System.out.println("Running code")
        val call = pipelineContext.call
        val runtime = call.parameters["runtime"]

        if (runtime == null) {
            call.respondText("Runtime not found")
            return
        }

        val multiPartData = call.receiveMultipart()
        multiPartData.forEachPart {
            when (it) {
                is PartData.FormItem -> {
                    println("Form field: ${it.name} = ${it.value}")
                }

                is PartData.FileItem -> {
                    val fileBytes = it.streamProvider().readBytes()
                    val runCodeRequest = RunCodeRequest(
                        runtime = runtime,
                        data = fileBytes
                    )

                    val runnableProgramResult = createRunnableProgram(runCodeRequest)

                    // If the runnable program creation succeeded, run the program in the sandbox
                    runnableProgramResult.onSuccess { runnableProgram ->

                        // Run the program in the sandbox
                        SandboxService.runRunnableProgramInSandbox(runnableProgram)
                            .onSuccess { sandboxResult ->
                                call.respondText(sandboxResult.toCodeResultJSON())
                            }.onFailure { error ->
                                call.respondText(error.toCodeResultJSON())
                            }

                    }.onFailure { error ->
                        when (error) {
                            is UnsupportedRuntime -> call.respondText("Error: Unsupported runtime")
                            is StartingCommandNotFound -> call.respondText("Error: Starting command not found for runtime")
                            else -> call.respondText("Error: ${error.message}")
                        }
                    }

                }

                else -> {}
            }
            it.dispose()
        }


    }

    private fun Throwable.toCodeResultJSON(): String {
        val runCodeResult = RunCodeResult(
            success = false,
            executionResult = this.message ?: "Unknown error"
        )

        return Json.encodeToString(runCodeResult)
    }

    private fun SandboxExecutionResult.toCodeResultJSON(): String {
        val runCodeResult = RunCodeResult(
            success = true,
            executionResult = this.executionResult
        )

        return Json.encodeToString(runCodeResult)
    }

    fun createRunnableProgram(runCodeRequest: RunCodeRequest): Result<RunnableProgram> {

        // Get the runtime from the request
        val runtime: GaiaRuntime = try {
            parseGaiaRuntime(runCodeRequest.runtime)
        } catch (e: IllegalArgumentException) {
            return Result.failure(UnsupportedRuntime("Unsupported runtime: ${runCodeRequest.runtime}"))
        }

        // Get the start command for the runtime
        val startCommand = try {
            runtime.getStartCommand()
        } catch (e: IllegalArgumentException) {
            return Result.failure(StartingCommandNotFound("Starting command not found for runtime: $runtime"))
        }

        val executionId = createExecutionId(runtime)
        val programFile = try {
            runtime.createTempInstanceName(executionId, runCodeRequest)
        } catch (e: Exception) {
            return Result.failure(Exception("Error creating temp instance name: ${e.message}"))
        }

        // Create deploy.gaia file
        try {
            File("uploads/$executionId/deploy.gaia").bufferedWriter()
                .use { out -> out.write(runtime.defaultDeployCommand) }
        } catch (e: Exception) {
            return Result.failure(Exception("Error creating deploy.gaia file: ${e.message}"))
        }

        // Return the runnable program params
        return Result.success(
            RunnableProgram(
                executionId = executionId,
                programDirectory = programFile,
                runtime = runtime,
                startCommand = startCommand
            )
        )
    }

    private fun createExecutionId(runtime: GaiaRuntime): String {
        val random = "${Math.random()}"
        return "${runtime.name}-$random"
    }

    private fun GaiaRuntime.createTempInstanceName(executionId: String, runCodeRequest: RunCodeRequest): File {
        val tempBasePath = File("uploads/$executionId/")
        tempBasePath.mkdirs()
        val initialFile = this.initialFile()
        val tmpFile = Paths.get(tempBasePath.toString(), initialFile)
        tmpFile.toFile().createNewFile()
        Files.write(tmpFile, runCodeRequest.data)
        return tmpFile.toFile()
    }

    private fun GaiaRuntime.initialFile(): String {
        return when (this) {
            LINUX -> "script.sh"
            NODE_JS -> "index.js"
            else -> {
                throw IllegalArgumentException("Unsupported runtime: $this")
            }
        }
    }

}
fun parseGaiaRuntime(runtime: String): GaiaRuntime {
    return when (runtime) {
        "linux" -> LINUX
        "node" -> NODE_JS
        else -> throw IllegalArgumentException("Unsupported runtime: $runtime")
    }
}


private fun GaiaRuntime.getStartCommand(): String {
    return when (this) {
        LINUX -> "sh"
        NODE_JS -> "node"
        else -> {
            throw IllegalArgumentException("Unsupported runtime: $this")
        }
    }
}
