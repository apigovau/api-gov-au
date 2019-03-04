package au.gov.api.conversation

import au.gov.api.config.Config
import au.gov.api.web.NaiveAPICaller
import au.gov.api.web.ResourceCache
import com.beust.klaxon.Klaxon
import org.springframework.stereotype.Component

data class ConversationsVM(val content: MutableList<Conversation>, val firstPage:Boolean, val lastPage:Boolean, val links:MutableList<Link>, val totalPages:Int)
data class Conversation(val title:String, val body:String, val state:String, val typeTag:String, val mainUserName:String, val mainUserImageURI:String, val numComments:Int, val lastUpdated:String)
data class Link(val rel:String, val href:String)

@Component
class ConversationRepository() {
    val baseRepoUri = Config.get("BaseRepoURI")

    var conversationsCache = ResourceCache(NaiveAPICaller(), 5, convert = { serial -> Klaxon().parse<ConversationsVM>(serial)!! })

    fun get(id:String) : ConversationsVM = conversationsCache.get(baseRepoUri +"colab/$id")
}
