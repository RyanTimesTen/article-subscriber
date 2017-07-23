package api.v1

import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.specs.ShouldSpec

class ArticleControllerTest : ShouldSpec() {

    private val ANY = "any"
    private val FACEBOOK_BLOG = "facebook-blog"
    private val TIME_TECH = "time-tech"
    private val TWILIO_BLOG = "twilio_blog"
    private val TWILIO_PRESS = "twilio-press"

    private val SOURCES = listOf(ANY, FACEBOOK_BLOG, TIME_TECH, TWILIO_BLOG, TWILIO_PRESS)

    init {

        "The Article Controller" {

            "given a proper request" {

                "for an article from any source with no query parameters" {
                    should("respond with one article") {
                        val response = ArticleController().get(ANY)

                        response.total shouldBe 1
                        response.articles.size shouldBe 1

                        response.articles.forEach { article ->
                            article.name shouldNotBe null
                            article.link shouldNotBe null

                            article.source shouldBe ANY
                        }
                    }
                }

                "for one article from any source with query parameter <number>" {
                    should("respond with one article") {
                        val response = ArticleController().get(ANY, number = 1)

                        response.total shouldBe 1
                        response.articles.size shouldBe 1

                        response.articles.forEach { article ->
                            article.name shouldNotBe null
                            article.link shouldNotBe null

                            article.source shouldBe ANY
                        }

                    }
                }

                "for multiple articles from any source" {
                    should("respond with the given amount of articles from random sources") {
                        val response = ArticleController().get(ANY, number = 5)

                        val sources = mutableListOf<String>()

                        response.articles.forEach { article ->
                            sources.add(article.source)
                        }

                        sources.size shouldNotBe sources.toSet().size
                    }
                }

                "for an article from a specified source" {
                    should("respond with one article from that source") {
                        SOURCES.forEach { source ->
                            val response = ArticleController().get(source)

                            response.total shouldBe 1

                            response.articles.size shouldBe 1

                            response.articles.first().source shouldBe source
                        }
                    }
                }

                "for multiple articles from a specified source" {
                    should("respond with the given amount of articles from that source") {
                        var numArticles= 2

                        SOURCES.forEach { source ->
                            val response = ArticleController().get(source, number = numArticles)

                            response.total shouldBe numArticles

                            response.articles.size shouldBe numArticles

                            response.articles.forEach { article ->
                                article.name shouldNotBe null
                                article.link shouldNotBe null

                                article.source shouldBe source
                            }

                            numArticles += 1
                        }
                    }
                }

                "for 0 articles" {
                    should("respond with no articles") {
                        val response = ArticleController().get(ANY)

                        response.total shouldBe 0

                        response.articles.size shouldBe 0
                    }
                }
            }

            "given an improper request" {
                "where the number of articles is less than 0" {
                    should("throw an invalid number exception") {
                        shouldThrow<Exception> {
                            ArticleController().get(ANY, number = -1)
                        }
                    }
                }

                "with an invalid source" {
                    should("throw an invalid source exception") {
                        shouldThrow<Exception> {
                            ArticleController().get("invalid-source")
                        }
                    }
                }
            }

        }

    }

}