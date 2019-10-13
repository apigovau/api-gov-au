package au.gov.api.repositories

import au.gov.api.config.Config
import au.gov.api.models.Definition
import au.gov.api.models.Domain
import au.gov.api.models.SearchResults
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.fasterxml.jackson.databind.ObjectMapper
import khttp.get
import org.springframework.stereotype.Component
import java.net.URLEncoder

@Component
class DefinitionRepository : IDefinitionRepository {

    private val baseRepoUri = Config.get("BaseRepoURI")

    override fun getDomains(acr: String): List<Domain> {
        var url = if (acr != "") baseRepoUri + "definitions/domains?domain=$acr" else baseRepoUri + "definitions/domains"
        var response = get(url)
        var output: MutableList<Domain> = mutableListOf()
        try {
            var rawArray = splitRawJsonToArray(response.text)
            val om = ObjectMapper()
            for (jsonItem in rawArray) {
                output.add(om.readValue(jsonItem, Domain::class.java))
            }
            return output
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Unable to parse domains")
        }
    }

    override fun getDefinition(identifier: String): Definition {
        var response = get(baseRepoUri + "definitions/definition/detail?id=$identifier")
        try {
            var output = ObjectMapper().readValue(response.text, Definition::class.java)
            return output
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Unable to parse definition")
        }
    }

    override fun getDefinitions(pageNumber: Int, pageSize: Int, domain: String): List<Definition> {
        var url = baseRepoUri + "definitions?pageNumber=$pageNumber&pageSize=$pageSize" + getDomainPramString(domain)
        var response = get(url)
        var output: MutableList<Definition> = mutableListOf()
        try {
            var rawArray = splitRawJsonToArray(response.text)
            val om = ObjectMapper()
            for (jsonItem in rawArray) {
                output.add(om.readValue(jsonItem, Definition::class.java))
            }
            return output
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Unable to parse definitions")
        }
    }

    override fun searchDefinitions(query: String, domain: String, page: Int, size: Int, raw: Boolean, ignoreSynonym: Boolean): SearchResults<Definition> {
        var url = baseRepoUri + "definitions/search?query=${URLEncoder.encode(query, "UTF-8")}&page=$page&size=$size&raw=$raw&ignoreSynonym=$ignoreSynonym" + getDomainPramString(domain)
        var response = get(url)
        var responseObj = Parser().parse(StringBuilder(response.text))
        var searchResults: MutableList<Definition> = mutableListOf()
        var resultsArray = (responseObj as Map<String, Any>).getValue("results") as JsonArray<JsonObject>
        if (resultsArray.count() > 0) {
            var definitionsJSON = splitRawJsonToArray(resultsArray.toJsonString())
            val om = ObjectMapper()
            for (jsonItem in definitionsJSON) {
                searchResults.add(om.readValue(jsonItem, Definition::class.java))
            }
        }

        val v = (responseObj as JsonObject).get("usedSynonyms") as Map<String, JsonArray<String>>

        var syns: MutableMap<String, List<String>> = mutableMapOf()
        for (key in v.keys) {
            var syn: MutableList<String> = mutableListOf()
            for (word in v[key] as Collection<String>) {
                syn.add(word)
            }
            syns[key] = syn
        }

        return SearchResults<Definition>(searchResults,
                (responseObj as Map<String, Int>).getValue("howManyResults"),
                syns.toMap())
    }

    override fun countDefinitions(domain: String): Int {
        var response = get(baseRepoUri + "definitions/definition/count${ if(domain != "") "?domain=$domain" else ""}")
        return response.text.toInt()
    }

    private fun getDomainPramString(domain: String): String {
        if (domain == "") return ""

        var output = ""

        if (domain.contains(' ')) {
            val splitDomains = domain.split(' ')

            for (domainAcr in splitDomains) {
                output += "&domain=$domainAcr"
            }
        } else {
            output += "&domain=$domain"
        }
        return output
    }

    private fun splitRawJsonToArray(json: String): List<String> {
        var rawJsonArray = json.substring(1, json.length - 1).split("},{").toMutableList()
        if (rawJsonArray.count() > 1) {
            rawJsonArray[0] = "${rawJsonArray.first()}}"
            rawJsonArray[rawJsonArray.count() - 1] = "{${rawJsonArray.last()}"
        }
        for (i in 1..rawJsonArray.count() - 2) {
            var text = rawJsonArray[i]
            rawJsonArray[i] = "{$text}"
        }
        return rawJsonArray.toList()
    }
}