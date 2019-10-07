package au.gov.api.models

data class SearchResults<T>(val results: List<T>, val howManyResults: Int, val usedSynonyms: Map<String, List<String>>? = null)