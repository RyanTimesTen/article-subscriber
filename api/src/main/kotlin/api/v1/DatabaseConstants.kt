package api.v1

val JDBC_DRIVER = "org.postgresql.Driver"
val DB_URL = "jdbc:postgresql://localhost:5432/article-subscriber"

val PG_USER: String? = System.getenv("PG_USER")
val PG_PASS: String? = System.getenv("PG_PASS")