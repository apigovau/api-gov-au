package au.gov.api.controllers

import au.gov.api.config.Config
import au.gov.api.models.*
import au.gov.api.services.DefinitionService
import au.gov.api.services.RelationshipService
import au.gov.api.services.SynonymService
import au.gov.api.services.SyntaxService
import au.gov.api.web.URLHelper
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.net.URL
import javax.servlet.http.HttpServletRequest
import khttp.responses.Response
import khttp.structures.authorization.BasicAuthorization
import java.util.*


@RestController
class DefinitionsRestController {

    @Autowired
    private lateinit var definitionService: DefinitionService

    @Autowired
    private lateinit var synonymService: SynonymService

    @Autowired
    private lateinit var syntaxService: SyntaxService

    @Autowired
    private lateinit var relationshipService: RelationshipService

    @Autowired
    private val request: HttpServletRequest? = null


    @ResponseStatus(HttpStatus.FORBIDDEN)
    class UnauthorisedToAccessMonitoring : RuntimeException()

    /* Legacy
    @CrossOrigin
    @GetMapping("/monitor")
    fun test_db_stats(@RequestParam authKey:String):Map<String, Any>{
        var authKeyEnv: String = System.getenv("authKey") ?: ""
        if(authKey != authKeyEnv) throw UnauthorisedToAccessMonitoring()

        val map = mutableMapOf<String,Any>()
        map["queryLogTableRows"] = queryLogger.numberOfQueries()
        map["definitionCount"] = definitionService.howManyDefinitions()

        return map
    }*/


    @CrossOrigin
    @GetMapping("/definitions/api/definition/{domain}/{id}")
    fun specificDefinition(@PathVariable domain: String, @PathVariable id: String): HateosResult<Definition> {
        val baseUrl = "${request?.scheme}://" +
                "${request?.serverName}" +
                if (request?.serverPort != 80 && request?.serverPort != 443) ":${request?.serverPort}" else ""

        val identifier = "http://api.gov.au/definition/$domain/$id"
        val definition = definitionService.getDefinition(identifier)

        return HateosResult(
            definition,
            listOf(
                Link("syntax", "$baseUrl/definitions/api/syntax/$domain/$id"),
                Link("relations", "$baseUrl/definitions/api/relations/$domain/$id")
            )
        )
    }

    @CrossOrigin
    @GetMapping("/definitions/api")
    fun browseDefinitions(@RequestParam(defaultValue = "20") size: Int,
                           @RequestParam(defaultValue = "0") page: Int): PaginationResult<HateosResult<Definition>> {
        val baseUrl = "${request?.scheme}://" +
                "${request?.serverName}" +
                if (request?.serverPort != 80 && request?.serverPort != 443) ":${request?.serverPort}" else ""

        val definitions = definitionService.getDefinitions(page, size)
        val definitionsHateos =  definitions.map {
            val domainAndNumber = it.identifier.replace("http://api.gov.au/definition/", "")

            HateosResult(
                content = it,
                links = listOf(
                    Link("syntax", "$baseUrl/definitions/api/syntax/$domainAndNumber"),
                    Link("relations", "$baseUrl/definitions/api/relations/$domainAndNumber")
                )
            )
        }
        return PaginationResult(definitionsHateos, URLHelper().getURL(request), definitionService.countDefinitions())
    }

    @CrossOrigin
    @GetMapping("/definitions/api/domains")
    fun domains(): List<Domain> {
        return definitionService.getDomains()
    }

    @CrossOrigin
    @GetMapping("/definitions/api/search")
    fun searchDefinitions(
            @RequestParam(defaultValue = "") query: String,
            @RequestParam(defaultValue = "") domain: String,
            @RequestParam(defaultValue = "20") size: Int,
            @RequestParam(defaultValue = "0") page: Int
    ): PaginationResult<Definition> {
        val results = definitionService.search(query, domain, page, size)
        return PaginationResult(results.results, URLHelper().getURL(request), results.howManyResults)
    }


    @CrossOrigin
    @GetMapping("/definitions/api/syntax/{domain}/{id}")
    fun specificDefinitionSyntax(@PathVariable domain: String, @PathVariable id: String): Syntax? {
        val identifier = "http://api.gov.au/definition/$domain/$id"
        return syntaxService.getSyntax(identifier)
    }


    @CrossOrigin
    @GetMapping("/definitions/api/relations/{domain}/{id}")
    fun relations(@PathVariable domain: String, @PathVariable id: String): Map<String, List<Relationship>> {
        val identifier = "http://api.gov.au/definition/$domain/$id"
        val relations = relationshipService.getRelationships(identifier)
        for (relation in relations.keys) {
            for (result in relations[relation]!!) {
                val definition = definitionService.getDefinition(result.to)
                result.toName = definition.name
            }
        }
        return relations
    }

    /*

    Example code to recieve files.
    Exploring this idea of synchronising between domains.

    @CrossOrigin
    @PostMapping("/api/domains/{domain}")
    fun specific_definition_syntax(@PathVariable domain:String, @RequestBody body: Any?): String {
        println(request)
        return "Seems ok."
    }
    */

    @CrossOrigin
    @GetMapping("/definitions/api/synonyms")
    fun synonyms(): List<List<String>> {
        return synonymService.getSynonyms()
    }

    @GetMapping("/definitions/api/imgRedirect.svg")
    fun imgRedirect(@RequestParam url: String): String {
        if (url.contains("localhost")) return """<svg id="graph00" xmlns="http://www.w3.org/2000/svg" width="200" height="50" version="1.1"><text x="5" y="20">No images in dev.</text></svg>"""
        return URL(url).readText()
    }

    @PostMapping("/definitions/api/definition/{domain}/{id}")
    fun postDefinition(request: HttpServletRequest, @PathVariable domain: String, @PathVariable id: String, @RequestBody definition: Any) : String
    {
        var url = ""
        if (id.toLowerCase()=="new") {
            url = Config.get("BaseRepoURI") + "definitions/definition?id=$domain&domainExists=true"
        } else {
            url = Config.get("BaseRepoURI") + "definitions/definition?id=$domain/$id&domainExists=true"
        }


        val x = redirectToUrl(request,url,definition)
        return x.text
    }

    //TODO: Confirm if this method is required. Does not work in production either
    /*@PostMapping("/definitions/api/relationships")
    fun postRelationship(request: HttpServletRequest, @RequestBody relationship: Any) : String {
        val url = Config.get("BaseRepoURI") + "definitions/relationships"
        val x = redirectToUrl(request,url,relationship)
        return x.text
    }*/

    //TODO: Confirm if this method is required. Does not work in production either
    /*@PostMapping("/definitions/api/syntax")
    fun postSyntax(request: HttpServletRequest, @RequestParam id: String, @RequestBody syntaxs: Any) : String {
        val url = Config.get("BaseRepoURI") + "definitions/syntax?id=$id"
        val x = redirectToUrl(request,url,syntaxs)
        return x.text
    }*/

    private fun getBasicAuthFromRequest(request: HttpServletRequest): BasicAuthorization{
        val raw = request.getHeader("authorization")
        val userAndPass = String(Base64.getDecoder().decode(raw.removePrefix("Basic "))).split(":")
        val user = userAndPass[0]
        val pass = userAndPass[1]
        return BasicAuthorization(user,pass)
    }
    private fun redirectToUrl(request: HttpServletRequest, url:String,payload:Any) : Response {
        val parser: Parser = Parser()
        val requestPayload: JsonObject = parser.parse(StringBuilder(Klaxon().toJsonString(payload))) as JsonObject
        val authn = getBasicAuthFromRequest(request)
        return khttp.post(url, auth = authn, json = requestPayload)
    }

}
