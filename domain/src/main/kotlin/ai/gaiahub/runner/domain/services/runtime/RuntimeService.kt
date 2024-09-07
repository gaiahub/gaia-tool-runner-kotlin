package ai.gaiahub.runner.domain.services.runtime

import ai.gaiahub.runner.domain.exceptions.RuntimeNotFoundException
import ai.gaiahub.runner.domain.models.GaiaRuntime

/**
 * Service to manage runtimes
 */
object RuntimeService {

    fun listAvailableRuntimes():List<GaiaRuntime> {
        return listOf(
            NODE_JS,
            LINUX
        )
    }

    fun getRuntimeByName(name:String):Result<GaiaRuntime> {
        listAvailableRuntimes().find { it.name == name }?.let {
            return Result.success(it)
        } ?: run {
            return Result.failure(RuntimeNotFoundException("Runtime not found"))
        }
    }

}


