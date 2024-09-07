package ai.gaiahub.runner.routes.models

import kotlinx.serialization.Serializable

@Serializable
data class RunCodeResult(
    val success: Boolean,
    val executionResult: String,
)
