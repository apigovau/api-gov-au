package au.gov.api.services

import au.gov.api.models.Syntax
import au.gov.api.repositories.SyntaxRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SyntaxService @Autowired constructor(private val repo: SyntaxRepository) {

    //TODO Update Repository project to return correct HTTP status codes to avoid exceptions being thrown
    fun getSyntax(identifier: String) : Syntax? = try { repo.getSyntax(identifier) } catch (_: Exception) { null }
}