package ai.gaiahub.runner.data.repositories.implementations

import ai.gaiahub.runner.data.repositories.contracts.VariableRepository
import ai.gaiahub.runner.data.repositories.implementations.entities.VariableEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ktorm.dsl.*
import java.util.UUID

class VariableRepositoryKtorm(): VariableRepository, KoinComponent {

    private val databaseConnection by inject<DatabaseConnection>()

    override fun getVariablesByUserId(userId: String): Result<List<Pair<String, String>>> {
        try {
            val variables = mutableListOf<Pair<String, String>>()
            databaseConnection.database!!
                .from(VariableEntity)
                .select()
                .where { VariableEntity.userId eq UUID.fromString(userId) }
                .forEach { row ->
                    val name = row[VariableEntity.name].toString()
                    val value = row[VariableEntity.value].toString()
                    variables.add(Pair(name, value))
                }
            return Result.success(variables)
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
    }
}