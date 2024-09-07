package ai.gaiahub.runner

import ai.gaiahub.runner.data.repositories.contracts.ToolRepository
import ai.gaiahub.runner.data.repositories.contracts.VariableRepository
import ai.gaiahub.runner.data.repositories.implementations.DatabaseConnection
import ai.gaiahub.runner.data.repositories.implementations.ToolsRepositoryKtorm
import ai.gaiahub.runner.data.repositories.implementations.VariableRepositoryKtorm
import ai.gaiahub.runner.data.repositories.implementations.entities.VariableEntity
import ai.gaiahub.runner.domain.services.tools.ToolsService
import ai.gaiahub.runner.domain.services.variables.VariablesService
import org.koin.dsl.module

val sharedModule = module {

    single<DatabaseConnection> { DatabaseConnection() }
    single<ToolRepository> { ToolsRepositoryKtorm() }
    single<VariableRepository> { VariableRepositoryKtorm() }
    single<ToolsService> { ToolsService(
        toolRepository = get()
    ) }
    single<VariablesService> { VariablesService(
        variableRepository = get()
    ) }

}