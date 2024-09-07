package ai.gaiahub.runner.data.repositories.implementations

import org.ktorm.database.Database
import org.ktorm.support.postgresql.PostgreSqlDialect

fun connectToDatabase(jdbcConnectionString: String): Database {
    println("Connecting to database with connection string: $jdbcConnectionString")
    return Database.connect(
        dialect = PostgreSqlDialect(),
        url = jdbcConnectionString,
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "postgres")
}

class DatabaseConnection {
    var database: Database? = null
}