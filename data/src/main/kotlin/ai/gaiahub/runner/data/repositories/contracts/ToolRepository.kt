package ai.gaiahub.runner.data.repositories.contracts

import ai.gaiahub.runner.data.repositories.contracts.dto.ToolSourceCodeDTO

interface ToolRepository {
    fun getAllTools(): Result<List<String>>
    fun getToolSourceCode(toolId: String): Result<ToolSourceCodeDTO>
}