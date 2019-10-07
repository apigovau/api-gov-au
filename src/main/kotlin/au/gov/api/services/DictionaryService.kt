package au.gov.api.services

import au.gov.api.models.Filters
import au.gov.api.repositories.IDictionaryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DictionaryService @Autowired constructor(private val repo: IDictionaryRepository) {

    fun getDictionaryCorrection(query: String, filters: Filters? = null): String = repo.getDictionaryCorrection(query, filters)
}