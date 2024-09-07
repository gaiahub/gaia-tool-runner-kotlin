package ai.gaiahub.runner.data.repositories.contracts

interface VariableRepository {
    fun getVariablesByUserId(userId: String): Result<List<Pair<String, String>>>
}