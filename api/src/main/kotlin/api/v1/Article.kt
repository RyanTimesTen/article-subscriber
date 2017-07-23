package api.v1

data class Article(val name: String, val link: String, val source: String)

data class ArticleResponse(val total: Int, val articles: List<Article>)