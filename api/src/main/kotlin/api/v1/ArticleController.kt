package api.v1

import mu.KotlinLogging
import org.springframework.web.bind.annotation.*
import java.sql.*

val JDBC_DRIVER = "org.postgresql.Driver"
val DB_URL = "jdbc:postgresql://localhost:5432/article-subscriber"

val PG_USER: String? = System.getenv("PG_USER")
val PG_PASS: String? = System.getenv("PG_PASS")

private val logger = KotlinLogging.logger {}

@RestController
class ArticleController {
    @GetMapping(value = "/api/v1/article/{source}")
    fun get(@PathVariable("source") source: String, @RequestParam(name = "number", defaultValue = "1") number: Int = 1): ArticleResponse {
        if (PG_USER == null || PG_PASS == null) {
            val e = "Unable to retrieve database username or password"
            logger.error { e }
            throw NullPointerException(e)
        }

        if (!SOURCES.contains(source)) {
            val e = "Invalid source: [$source]"
            logger.error { e }
            throw IllegalArgumentException(e)
        }

        if (number < 0) {
            val e = "Invalid number: [$number]"
            logger.error { e }
            throw IllegalArgumentException(e)
        }

        val articles: MutableList<Article> = mutableListOf()

        var connection: Connection? = null
        var statement: Statement? = null
        var result: ResultSet? = null

        try {
            // Need to do this, not sure why
            Class.forName(JDBC_DRIVER)

            connection = DriverManager.getConnection(DB_URL, PG_USER, PG_PASS)
            logger.info { "Successfully connected to database" }

            statement = connection!!.createStatement()

            val query = "SELECT * FROM \"$source\" LIMIT $number;"

            result = statement!!.executeQuery(query)

            if (result != null) {
                while (result.next()) {
                    val name = result.getString("name")
                    val link = result.getString("link")
//                    val source = result.getString("source")

                    logger.info { "Adding article with name: [$name], link: [$link], and source: [$source]" }
                    articles.add(Article(name, link, source))
                }
            }
        } catch (e: SQLException) {
            logger.error { "SQL error occured: ${e.printStackTrace()}" }
            throw SQLException()
        } catch (e: Exception) {
            logger.error { "Error occured: ${e.printStackTrace()}" }
            throw Exception()
        } finally {
            if (result != null) {
                result.close()
            }

            if (statement != null) {
                statement.close()
            }

            if (connection != null) {
                connection.close()
            }
        }

        return ArticleResponse(articles.size, articles)
    }
}