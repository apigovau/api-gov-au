package au.gov.api.repositories

interface ISynonymRepository {
    fun getSynonyms(): List<List<String>>
}