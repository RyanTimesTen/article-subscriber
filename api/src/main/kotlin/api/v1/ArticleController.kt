package api.v1

import org.springframework.web.bind.annotation.*

@RestController
class ArticleController {
    @RequestMapping(value = "/api/v1/article/{source}", method = arrayOf(RequestMethod.GET))
    fun article_get_handler(@PathVariable("source") source: String): String {
        return "You requested an article from $source"
    }
}