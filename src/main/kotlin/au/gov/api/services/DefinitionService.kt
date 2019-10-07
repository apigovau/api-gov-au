package au.gov.api.services

import au.gov.api.models.*
import au.gov.api.repositories.IDefinitionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DefinitionService @Autowired constructor(private val repo: IDefinitionRepository) {

    fun getDomains(acr: String = ""): List<Domain> = repo.getDomains(acr)

    fun getDefinition(identifier: String): Definition = repo.getDefinition(identifier)

    fun getDefinitions(pageNumber: Int, pageSize: Int, domain: String = ""): List<Definition> = repo.getDefinitions(pageNumber, pageSize, domain)

    fun search(query: String, domain: String, page: Int, size: Int, raw: Boolean = false, ignoreSynonym: Boolean = false): SearchResults<Definition> = repo.searchDefinitions(query, domain, page, size, raw, ignoreSynonym)

    fun countDefinitions(domain: String = ""): Int = repo.countDefinitions(domain)
}