package api.v1

import com.twilio.twiml.Body
import com.twilio.twiml.Message
import com.twilio.twiml.MessagingResponse
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class SMSController {

    @RequestMapping(value = "/handleRequest", produces = arrayOf("text/xml"))
    fun handleRequest(request: HttpServletRequest): String? {
        val body = request.getParameter("Body")

        val commandData = body.split(Regex("\\s"))

        if (commandData.isEmpty()) {
            return emptyResponse()
        }

        val operation = commandData[0]

        return when (operation.toLowerCase()) {
            "list" -> returnSources()
            "get" -> {
                val source = commandData[1].toLowerCase() + '-' + commandData[2].toLowerCase()
                var latest = false
                var number = 1

                if (commandData.size > 3) {
                    try {
                        number = commandData[3].toInt()
                    } catch (e: NumberFormatException) {
                        latest = commandData[3].contains("latest", ignoreCase = true)
                    }

                    if (commandData.size > 4) number = commandData[4].toInt()
                }

                returnArticles(source, latest, number)
            }
            else -> returnUnimplemented()
        }
    }

    private fun emptyResponse(): String {
        val messageBody = "Do you want an article or what!?"
        return buildAndReturnResponse(messageBody)
    }

    private fun returnSources(): String {
        val messageBody = "Available sources are: ${displaySources()}"
        return buildAndReturnResponse(messageBody)
    }

    private fun returnArticles(source: String, latest: Boolean = false, number: Int = 1): String {
        if (SOURCES.contains(source)) {
            return getArticleResponseMessage(source, latest, number)
        }
        val messageBody = "Source not available yet!\nAvailable sources are: ${displaySources()}"
        return buildAndReturnResponse(messageBody)
    }

    private fun displaySources() = SOURCES.map { it.replace('-', ' ') }.joinToString { it }

    private fun returnUnimplemented(): String {
        val messageBody = "This operation is not implemented yet!\nAvailable command are \"list\" and \"get {source}\""
        return buildAndReturnResponse(messageBody)
    }

    private fun getArticleResponseMessage(source: String, latest: Boolean, number: Int): String {
        val articleResponse = ArticleController().get(source, latest, number)
        var messageBody = ""

        articleResponse.articles.forEach { messageBody += "${it.name}\n${it.link}\n\n" }

        return buildAndReturnResponse(messageBody)
    }

    private fun buildAndReturnResponse(messageBody: String): String {
        val message = Message.Builder().body(Body(messageBody)).build()
        return MessagingResponse.Builder().message(message).build().toXml()
    }
}