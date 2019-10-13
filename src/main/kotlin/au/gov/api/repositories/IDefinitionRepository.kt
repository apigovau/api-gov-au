package au.gov.api.repositories

import au.gov.api.models.Definition
import au.gov.api.models.Domain
import au.gov.api.models.SearchResults

interface IDefinitionRepository {
    fun getDomains(acr: String = ""): List<Domain>
    fun getDefinition(identifier: String): Definition
    fun getDefinitions(pageNumber: Int, pageSize: Int, domain: String = ""): List<Definition>
    fun searchDefinitions(query: String, domain: String, page: Int, size: Int, raw: Boolean = false, ignoreSynonym: Boolean = false): SearchResults<Definition>
    fun countDefinitions(domain: String = ""): Int
}