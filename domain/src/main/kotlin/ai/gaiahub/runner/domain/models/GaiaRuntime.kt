package ai.gaiahub.runner.domain.models

data class GaiaRuntime(
    val name: String,
    val version: String,
    val description: String,
    val imageName: String,
    val baseImage: String,
    val defaultDeployCommand: String,
)