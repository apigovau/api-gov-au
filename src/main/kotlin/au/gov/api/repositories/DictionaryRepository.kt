package au.gov.api.repositories

import au.gov.api.config.Config
import au.gov.api.models.Filters
import khttp.get
import org.springframework.stereotype.Component
import java.net.URLEncoder

@Component
class DictionaryRepository : IDictionaryRepository {

    private val baseRepoUri = Config.get("BaseRepoURI")

    override fun getDictionaryCorrection(query: String, filters: Filters?): String {
        val url = baseRepoUri + "definitions/dict?query=${URLEncoder.encode(query, "UTF-8")}" + filterPramConstructor(filters)
        var response = get(url)
        return response.text
    }

    private fun filterPramConstructor(filters: Filters?): String {
        var output = ""

        return if (filters == null) {
            output
        } else {
            for (domain in filters.Domains) {
                output += "&domains=${domain.acronym}"
            }
            output
        }
    }
}