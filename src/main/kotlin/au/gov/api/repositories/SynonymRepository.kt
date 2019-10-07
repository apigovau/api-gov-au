package au.gov.api.repositories

import au.gov.api.config.Config
import au.gov.api.web.NaiveAPICaller
import au.gov.api.web.ResourceCache
import com.beust.klaxon.Klaxon
import org.springframework.stereotype.Component

@Component
class SynonymRepository : ISynonymRepository {

    private val baseUri = Config.get("BaseRepoURI")
    private val synonyms = ResourceCache(NaiveAPICaller(), 5, convert = { serial -> Klaxon().parseArray<List<String>>(serial)!! })

    override fun getSynonyms(): List<List<String>> = synonyms.get("$baseUri/definitions/synonyms")
}