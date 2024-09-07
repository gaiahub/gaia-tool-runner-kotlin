package ai.gaiahub.runner.data.repositories.implementations.entities

import org.ktorm.schema.Table
import org.ktorm.schema.text
import org.ktorm.schema.uuid

object ToolEntity : Table<Nothing>("tool") {
    val id = text("id").primaryKey()
    val code = text("code")
    val func = text("func")
    val userId = uuid("userId")
}