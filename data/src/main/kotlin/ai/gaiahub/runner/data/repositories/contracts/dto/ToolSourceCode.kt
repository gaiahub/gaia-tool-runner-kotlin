package ai.gaiahub.runner.data.repositories.contracts.dto

import java.util.UUID

data class ToolSourceCodeDTO(
    val userId: UUID,
    val sourceCode: String,
)
