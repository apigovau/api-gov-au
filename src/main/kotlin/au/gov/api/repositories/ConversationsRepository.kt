package au.gov.api.repositories

import au.gov.api.config.Config
import au.gov.api.models.Comment
import au.gov.api.models.Comments
import au.gov.api.models.Conversations
import au.gov.api.web.NaiveAPICaller
import au.gov.api.web.ResourceCache
import com.beust.klaxon.Klaxon
import com.vladsch.flexmark.ext.gfm.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet
import org.springframework.stereotype.Component

@Component
class ConversationsRepository : IConversationsRepository {

    private val baseRepoUri = Config.get("BaseRepoURI")
    private var conversationsCache = ResourceCache(NaiveAPICaller(), 5, convert = { serial -> Klaxon().parse<Conversations>(serial)!! })
    private var commentsCache = ResourceCache(NaiveAPICaller(), 5, convert = { serial -> Klaxon().parse<Comments>(serial)!! })

    override fun getConversations(id: String, page:Int): Conversations {
        val conversations = conversationsCache.get(baseRepoUri + "colab/$id?size=5&page=$page")

        conversations.content.forEach { conversation ->
            val totalComments : MutableList<Comment>

            var commentPage = 1
            var comments = commentsCache.get(baseRepoUri + "colab/$id/comments?"
                    + "convoId=${conversation.id}&"
                    + "convoType=${conversation.typeTag}&"
                    + "size=100&"
                    + "page=$commentPage"
            )
            totalComments = comments.content

            while (!comments.lastPage)
            {
                commentPage++
                comments = commentsCache.get(baseRepoUri + "colab/$id/comments?"
                        + "convoId=${conversation.id}&"
                        + "convoType=${conversation.typeTag}"
                        + "size=100&"
                        + "page=$commentPage"
                )

                comments.content.forEach { it.body = getHTMLFromMarkDown(it.body) }

                totalComments.addAll(comments.content)
            }

            conversation.comments = totalComments
            conversation.body = getHTMLFromMarkDown(conversation.body)
        }

        return conversations
    }

    companion object{
        @JvmStatic
        fun getHTMLFromMarkDown(markdown:String):String {

            val options = MutableDataSet()
            options.set(Parser.EXTENSIONS, listOf(TablesExtension.create()));
            options.set(HtmlRenderer.GENERATE_HEADER_ID, true)
            options.set(HtmlRenderer.RENDER_HEADER_ID, true)

            val parser = Parser.builder(options).build()
            val renderer = HtmlRenderer.builder(options).build()

            // You can re-use parser and renderer instances
            val document = parser.parse(markdown)
            return renderer.render(document)
        }
    }
}