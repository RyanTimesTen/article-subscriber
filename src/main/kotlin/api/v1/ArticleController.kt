package api.v1

import mu.KotlinLogging
import org.springframework.web.bind.annotation.*
import java.sql.*
import java.util.Random

@RestController
class ArticleController {
    private val logger = KotlinLogging.logger {}

    @GetMapping(value = "/api/v1/article/{source}")
    fun get(@PathVariable("source") sourceOrAny: String, @RequestParam(name = "number", defaultValue = "1") number: Int = 1): ArticleResponse {
        checkForErrors(sourceOrAny, number)

        val articles = queryDatabaseForArticlesFrom(getSource(sourceOrAny), number)

        return ArticleResponse(articles.size, articles)
    }

    private fun queryDatabaseForArticlesFrom(source: String, number: Int): List<Article> {
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

            addArticles(articles, source, result)
        } catch (e: SQLException) {
            logger.error { "SQL error occured: ${e.printStackTrace()}" }
            throw SQLException()
        } catch (e: Exception) {
            logger.error { "Error occured: ${e.printStackTrace()}" }
            throw Exception()
        } finally {
            result?.close()
            statement?.close()
            connection?.close()
        }

        return articles
    }

    private fun addArticles(articles: MutableList<Article>, source: String, result: ResultSet?) {
        if (result != null) {
            while (result.next()) {
                val name = result.getString("name")
                val link = result.getString("link")

                logger.info { "Adding article with name: [$name], link: [$link], and source: [$source]" }
                articles.add(Article(name, link, source))
            }
        }
    }

    private fun checkForErrors(sourceOrAny: String, number: Int) {
        if (PG_USER == null || PG_PASS == null) {
            val e = "Unable to retrieve database username or password"
            logger.error { e }
            throw NullPointerException(e)
        }

        if (!SOURCES.contains(sourceOrAny) && sourceOrAny != ANY) {
            val e = "Invalid source: [$sourceOrAny]"
            logger.error { e }
            throw IllegalArgumentException(e)
        }

        if (number < 0) {
            val e = "Invalid number: [$number]"
            logger.error { e }
            throw IllegalArgumentException(e)
        }
    }

    private fun getSource(source: String) = if (source == ANY) {
        SOURCES[Random().nextInt(SOURCES.size)]
    } else {
        source
    }
}