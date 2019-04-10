package au.gov.api.conversation

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ConversationService {

    @Autowired
    lateinit var repo: ConversationRepository

    fun get(id:String, page:Int): ConversationsVM? {
        return repo.get(id, page)
    }
}