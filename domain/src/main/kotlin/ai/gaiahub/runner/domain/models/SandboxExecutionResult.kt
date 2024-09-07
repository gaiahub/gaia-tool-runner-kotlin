package ai.gaiahub.runner.domain.models

data class SandboxExecutionResult(
    val sandboxId: String,
    val sandboxExecutionTempFolder: String,
    val executedSandbox: SandboxExecutionParams,
    val executionResult: String
)