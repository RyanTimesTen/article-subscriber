package api.v1

import org.springframework.web.bind.annotation.*

@RestController
class ArticleController {
    @GetMapping(value = "/api/v1/article/{source}")
    fun get(@PathVariable("source") source: String, @RequestParam(name = "number", defaultValue = "1") number: Int = 1): ArticleResponse {
        return ArticleResponse(0, emptyList())
    }
}