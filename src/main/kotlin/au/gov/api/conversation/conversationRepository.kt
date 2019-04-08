package au.gov.api.conversation

import au.gov.api.config.Config
import au.gov.api.web.NaiveAPICaller
import au.gov.api.web.ResourceCache
import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*

data class ConversationsVM(val content: MutableList<Conversation>, val firstPage:Boolean, val lastPage:Boolean, val links:MutableList<Link>, val totalPages:Int) {
    fun getUniqueTags(): List<String> = content
            .flatMap { conversation -> conversation.tags }
            .distinct()
}

data class Comments(val content: MutableList<Comment>, val firstPage:Boolean, val lastPage:Boolean, val links:MutableList<Link>, val totalPages:Int)
data class Conversation(val id:Int, val title: String, val body: String, val state: String, val typeTag: String, val tags: MutableList<String>, val mainUserName: String, val mainUserImageURI: String, val numComments: Int, val lastUpdated: String, @Json(ignored = true)var comments:MutableList<Comment> = mutableListOf())
data class Link(val rel: String, val href: String)
data class Comment(val username:String, val userImageURI:String, val created_at:String, val body:String) {
    fun postedOnString() : String {
        val dateCreated = LocalDateTime.parse(created_at, DateTimeFormatter.ISO_DATE_TIME)

        val duration = Duration.between(dateCreated, LocalDateTime.now())

        if (duration.toMinutes() < 60){
            return "posted ${duration.toMinutes()} minutes ago"
        }

        if (duration.toHours() < 24) {
            return "posted ${duration.toHours()} hours ago"
        }

        return "posted on the ${dateCreated.dayOfMonth}/${dateCreated.monthValue}/${dateCreated.year}"
    }
}

@Component
class ConversationRepository {
    private val baseRepoUri = Config.get("BaseRepoURI")
    private var conversationsCache = ResourceCache(NaiveAPICaller(), 5, convert = { serial -> Klaxon().parse<ConversationsVM>(serial)!! })
    private var commentsCache = ResourceCache(NaiveAPICaller(), 5, convert = { serial -> Klaxon().parse<Comments>(serial)!! })

    fun get(id: String, page:Int): ConversationsVM {
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

                totalComments.addAll(comments.content)
            }

            conversation.comments = totalComments
        }

        return conversations
    }
}