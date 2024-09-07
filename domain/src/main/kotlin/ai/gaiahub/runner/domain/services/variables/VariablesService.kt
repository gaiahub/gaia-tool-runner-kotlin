package ai.gaiahub.runner.domain.services.variables

import ai.gaiahub.runner.data.repositories.contracts.VariableRepository
import org.koin.core.component.KoinComponent

class VariablesService(private val variableRepository: VariableRepository): KoinComponent {

    fun getVariablesByUserId(userId: String): Result<List<Pair<String, String>>> {
        return variableRepository.getVariablesByUserId(userId)
    }
}