package ai.gaiahub.runner.domain.services.tools

import ai.gaiahub.runner.data.repositories.contracts.ToolRepository
import ai.gaiahub.runner.data.repositories.contracts.dto.ToolSourceCodeDTO
import ai.gaiahub.runner.domain.services.variables.VariablesService
import org.koin.core.component.KoinComponent

class ToolsService(private val toolRepository: ToolRepository, private val variablesService: VariablesService): KoinComponent {

    fun listAvailableTools(): Result<List<String>> {
        return  toolRepository.getAllTools()
    }

    fun getToolSourceCode(toolId: String): Result<String> {
        val result =  toolRepository.getToolSourceCode(toolId)
        result.let { it ->
            it.onSuccess {
                return Result.success(replaceVariables(it))
            }
            it.onFailure {
                return Result.failure(it)
            }
        }
        return Result.failure(Exception("Tool not found"))
    }

    private fun replaceVariables(it: ToolSourceCodeDTO): String {
        var sourceCode = it.sourceCode
        val userId = it.userId.toString()
        val variables = variablesService.getVariablesByUserId(userId)
        variables.onSuccess {
            it.forEach { variable ->
                sourceCode = sourceCode.replace("\${${variable.first}}", variable.second)
            }
        }
        return sourceCode
    }
}