package au.gov.api.models

data class Conversations(val content: MutableList<Conversation>, val firstPage:Boolean, val lastPage:Boolean, val links:MutableList<Link>, val totalPages:Int) {
    fun getUniqueTags(): List<String> = content
            .flatMap { conversation -> conversation.tags }
            .distinct()
}