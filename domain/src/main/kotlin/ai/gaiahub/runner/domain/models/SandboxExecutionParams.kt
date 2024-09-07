package ai.gaiahub.runner.domain.models

import ai.gaiahub.runner.domain.models.RunnableProgram

data class SandboxExecutionParams(
    val runnableProgram: RunnableProgram,
    val sandboxId: String,
)