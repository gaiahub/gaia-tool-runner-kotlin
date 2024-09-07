package ai.gaiahub.runner.routes.models

data class RunCodeRequest(
    val runtime: String,
    val data: ByteArray,
)
