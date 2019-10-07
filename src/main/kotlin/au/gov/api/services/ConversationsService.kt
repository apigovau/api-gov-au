package au.gov.api.services

import au.gov.api.models.Conversations
import au.gov.api.repositories.IConversationsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ConversationsService @Autowired constructor(private val repo: IConversationsRepository) {

    fun getConversations(id:String, page:Int): Conversations? {
        return repo.getConversations(id, page)
    }
}