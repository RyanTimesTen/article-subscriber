package api.v1

import io.kotlintest.specs.ShouldSpec

class ArticleControllerTest : ShouldSpec() {
    init {
        "The Article Controller" {
            "given a proper request" {
                "for an article from any source" {
                    should("respond with one article") {

                    }
                }

                "for multiple articles from any source" {
                    should("respond with the given amount of articles from random sources") {

                    }
                }

                "for an article from a specified source" {
                    should("respond with one article from that source") {

                    }
                }

                "for multiple articles from a specified source" {
                    should("respond with the given amount of articles from that source") {

                    }
                }
            }

            "given an improper request" {
                "where the number of articles is less than 1" {
                    should("respond with a number-related error message") {

                    }
                }

                "with an invalid query parameter" {
                    should("respond with a parameter-related error message") {

                    }
                }

                "with an invalid source" {
                    should("respond with a source-related error message") {

                    }
                }
            }
        }
    }
}