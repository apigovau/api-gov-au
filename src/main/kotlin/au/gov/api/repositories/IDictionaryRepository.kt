package au.gov.api.repositories

import au.gov.api.models.Filters

interface IDictionaryRepository {
    fun getDictionaryCorrection(query: String, filters: Filters? = null): String
}