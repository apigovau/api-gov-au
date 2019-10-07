package au.gov.api.repositories

import au.gov.api.models.Conversations

interface IConversationsRepository {
    fun getConversations(id: String, page: Int): Conversations
}