package ai.gaiahub.runner.domain.models

import ai.gaiahub.runner.domain.models.GaiaRuntime
import java.io.File

data class RunnableProgram(
    // The Language the program was written
    val executionId: String,

    // The program directory in the host machine
    val runtime: GaiaRuntime,

    // The command to run the program
    val programDirectory: File,
    val startCommand: String
)
