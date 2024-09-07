package ai.gaiahub.runner.data.repositories.implementations.entities

import org.ktorm.schema.Table
import org.ktorm.schema.text
import org.ktorm.schema.uuid

object VariableEntity : Table<Nothing>("variable") {
    val id = text("id").primaryKey()
    val name = text("name")
    val value = text("value")
    val type = text("type")
    val userId = uuid("userId")
}