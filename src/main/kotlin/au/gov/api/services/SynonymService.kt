package au.gov.api.services

import au.gov.api.repositories.ISynonymRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SynonymService @Autowired constructor(private val repo: ISynonymRepository) {

    fun getSynonyms() : List<List<String>> = repo.getSynonyms()
}