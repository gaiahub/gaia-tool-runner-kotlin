package ai.gaiahub.runner.data.repositories.implementations

import ai.gaiahub.runner.data.repositories.contracts.ToolRepository
import ai.gaiahub.runner.data.repositories.contracts.dto.ToolSourceCodeDTO
import ai.gaiahub.runner.data.repositories.implementations.entities.ToolEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.dsl.from
import org.ktorm.dsl.select
import java.util.UUID


class ToolsRepositoryKtorm(): ToolRepository, KoinComponent {

    private val databaseConnection by inject<DatabaseConnection>()

    override fun getAllTools(): Result<List<String>> {
        try {
            val availableToolsList: MutableList<String> = mutableListOf()
            for (row in databaseConnection.database!!.from(ToolEntity).select()) {
                availableToolsList.add(row[ToolEntity.id].toString())
            }
            return Result.success(availableToolsList)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
    }

    override fun getToolSourceCode(toolId: String): Result<ToolSourceCodeDTO> {
        try {
            for (row in databaseConnection.database!!.from(ToolEntity).select()) {
                val entryId = row[ToolEntity.id].toString()
                if (entryId == toolId) {
                    val sourceCode = row[ToolEntity.func].toString()
                    val userId = UUID.fromString(row[ToolEntity.userId].toString())
                    return Result.success(ToolSourceCodeDTO(userId, sourceCode))
                }
            }
            return Result.failure(Exception("Tool not found"))
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
    }
}