package ai.gaiahub.runner.domain.services.sandbox

import ai.gaiahub.runner.domain.models.RunnableProgram
import ai.gaiahub.runner.domain.models.SandboxExecutionParams
import ai.gaiahub.runner.domain.models.SandboxExecutionResult
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * This class is responsible for running a program in a sandbox.
 */
object SandboxService {

    fun runRunnableProgramInSandbox(runnableProgram: RunnableProgram): Result<SandboxExecutionResult> {
        val sandboxExecutionParams = SandboxExecutionParams(runnableProgram, runnableProgram.executionId)
        return runProgramInSandbox(sandboxExecutionParams)
    }

    private fun runProgramInSandbox(sandboxExecutionParams: SandboxExecutionParams): Result<SandboxExecutionResult> {
        try {
            val sandboxId = Math.random()
            val sourceFolderPath = sandboxExecutionParams.runnableProgram.programDirectory.toPath()
            val tempInstanceName = sandboxExecutionParams.sandboxId

            // Make the absolute path
            val absoluteFilePath = sourceFolderPath.parent.toAbsolutePath().toString() + "/."

            // Decide which runtime to use
            val runtime: String = getRuntimeToRun(sandboxExecutionParams)

            // Run the tool in a sandbox!
            val result = runSandbox(runtime, absoluteFilePath, tempInstanceName)
            val regex = Regex("(?<=PROGRAM-OUTPUT-BEGIN\\n)(.*?)(?=\\nPROGRAM-OUTPUT-END)", RegexOption.DOT_MATCHES_ALL)
            val matchResult = regex.find(result)
            val extractedContent = matchResult?.value ?: ""

            val sandboxResult = SandboxExecutionResult(
                sandboxId = sandboxId.toString(),
                sandboxExecutionTempFolder = sourceFolderPath.toString(),
                executedSandbox = sandboxExecutionParams,
                executionResult = extractedContent
            )

            return Result.success(sandboxResult)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun callSandbox(command: String): String {
        println(command)
        val processBuilder = ProcessBuilder("/bin/sh", "-c", "unset DOCKER_HOST && $command")
        val process = processBuilder.start()
        val output = StringBuilder()

        BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }
        }

        val exitCode = process.waitFor()
        if (exitCode != 0) {
            BufferedReader(InputStreamReader(process.errorStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    output.append(line).append("\n")
                }
            }
            throw RuntimeException("Command exited with code $exitCode\nError: $output")
        }

        return output.toString()
    }

    private fun runSandbox(runtime: String, absoluteFilePath: String, tempInstanceName: String): String {
        return callSandbox("./docker/tool-runner-controller/sandbox.sh -rg $runtime $absoluteFilePath $tempInstanceName")
    }

    fun restartDockerDaemon(): String {
        return callSandbox("dockerd")
    }

    fun createSandboxes(): String {
        return callSandbox("/./app/docker/tool-runner-controller/sandbox.sh -c /./app/docker/runners/nodejs/Dockerfile node /./app/docker/runners/nodejs/.")
    }

    fun getRuntimeToRun(sandboxExecutionParams: SandboxExecutionParams): String {
        return sandboxExecutionParams.runnableProgram.runtime.name
    }
}