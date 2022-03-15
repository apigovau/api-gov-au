package au.gov.api.controllers

import au.gov.api.models.*
import au.gov.api.services.*
import au.gov.api.web.URLHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest


@Controller
class DefinitionsController {

    @Autowired
    private lateinit var definitionService: DefinitionService

    @Autowired
    private lateinit var dictionaryService: DictionaryService

    @Autowired
    private lateinit var synonymService: SynonymService

    @Autowired
    private lateinit var syntaxService: SyntaxService

    @Autowired
    private lateinit var relationshipService: RelationshipService

    @Autowired
    private val request: HttpServletRequest? = null

    @RequestMapping("/definitions")
    fun searchSubmit(@ModelAttribute search: Search,
                     model: MutableMap<String, Any>,
                     @RequestParam(defaultValue = "20") size: Int,
                     @RequestParam(defaultValue = "0") page: Int,
                     @RequestParam(defaultValue = "false") raw: Boolean): String {
        val searchString = search.getQuery() ?: ""
        val filter = getFilterModel(search)
        model["search"] = Search()
        model["domains"] = definitionService.getDomains()
        model["filter"] = filter
        if (searchString != "") {
            model["showResults"] = "true"
            model["queryString"] = searchString
            val results = definitionService.search(searchString, search.getDomainSearchQuery(), page, size, raw, search.getIgnoreSynonym(false))
            val pageResult = PaginationResult(results.results, URLHelper().getURL(request), results.howManyResults)
            populateResultsPage(pageResult, model, searchString, filter)
            if (results.usedSynonyms != null) model["usedSynonyms"] = results.usedSynonyms
        }

        return "search"
    }


    @RequestMapping("/definitions/help")
    fun help(): String = "help"

    @RequestMapping("/definitions/browse")
    internal fun definitions(@ModelAttribute search: Search,
                             model: MutableMap<String, Any>,
                             @RequestParam(defaultValue = "20") size: Int,
                             @RequestParam(defaultValue = "0") page: Int): String {
        val searchString = search.getQuery() ?: ""
        val filter = getFilterModel(search)
        model["search"] = Search()
        model["filter"] = filter
        if (searchString != "") {
            model["action"] = "/definitions/browse"
            model["showResults"] = "true"
            model["queryString"] = searchString
            val results = definitionService.search(searchString, search.getDomainSearchQuery(), page, size, false, search.getIgnoreSynonym(false))
            val pageResult = PaginationResult(results.results, URLHelper().getURL(request), results.howManyResults)
            populateResultsPage(pageResult, model, searchString, filter)
            if (results.usedSynonyms != null) model["usedSynonyms"] = results.usedSynonyms
        } else {
            val pageResult = PaginationResult(definitionService.getDefinitions(page, size), URLHelper().getURL(request), definitionService.countDefinitions())
            populateResultsPage(pageResult, model, filter = filter)
        }
        return "browse"
    }


    @RequestMapping("/definitions/browse/{domain}")
    internal fun definitionsForDomain(
            @ModelAttribute search: Search,
            model: MutableMap<String, Any>,
            @RequestParam(defaultValue = "20") size: Int,
            @RequestParam(defaultValue = "0") page: Int,
            @PathVariable domain: String): String {
        val searchString = search.getQuery() ?: ""
        val domainName = definitionService.getDomains(domain).singleOrNull()?.name ?: "No domain called '$domain' "
        val filter = getFilterModel(search)
        if (domainName != "") model["domainName"] = domainName
        model["search"] = Search()
        model["filter"] = filter
        if (searchString != "") {
            model["action"] = "/definitions/browse/$domain"
            model["showResults"] = "true"
            model["queryString"] = searchString
            val results = definitionService.search(searchString, domain, page, size, false, search.getIgnoreSynonym(false))
            val pageResult = PaginationResult(results.results, URLHelper().getURL(request), results.howManyResults)
            populateResultsPage(pageResult, model, searchString, filter)
            if (results.usedSynonyms != null) model["usedSynonyms"] = results.usedSynonyms
        } else {
            val pageResult = PaginationResult(definitionService.getDefinitions(page, size, domain), URLHelper().getURL(request), definitionService.countDefinitions(domain))
            populateResultsPage(pageResult, model, filter = filter)
        }
        return "browse"
    }

    private fun populateResultsPage(pageResult: PaginationResult<Definition>, model: MutableMap<String, Any>, queryString: String = "", filter: Filters? = null) {
        val definitions = pageResult.content
        if (!pageResult.isFirstPage()) model["prevPage"] = pageResult.thePrevPage(false)
        if (!pageResult.isLastPage()) model["nextPage"] = pageResult.theNextPage(false)
        model["pageNumber"] = pageResult.pageNumber
        model["pageURL"] = pageResult.uri
        model["lastPageNumber"] = pageResult.getTotalPages()
        model["totalResults"] = pageResult.numberOfElements
        model["spellCheck"] = if (pageResult.numberOfElements == 0) dictionaryService.getDictionaryCorrection(queryString, filter) else ""

        val pagesToTheLeft = pageResult.pagesToTheLeft()
        if (pagesToTheLeft.size == 1) {
            model["leftPage"] = pagesToTheLeft[0]
            model["leftPageNumber"] = 1
        }
        if (pagesToTheLeft.size == 2) {
            model["leftPage"] = pagesToTheLeft[1]
            model["leftPageNumber"] = pageResult.pageNumber - 1
            model["firstPage"] = pageResult.theFirstPage()
        }

        val pagesToTheRight = pageResult.pagesToTheRight()
        if (pagesToTheRight.size == 1) {
            model["rightPage"] = pagesToTheRight[0]
            model["rightPageNumber"] = pageResult.getTotalPages()
        }
        if (pagesToTheRight.size == 2) {
            model["rightPage"] = pagesToTheRight[1]
            model["rightPageNumber"] = pageResult.pageNumber + 1
            model["lastPage"] = pageResult.theLastPage()
        }


        val maxLength = 200

        data class ViewDefinition(val name: String, val domain: String, val definition: String, val identifier: String, val status: String, val type: String)

        val viewDefns = mutableListOf<ViewDefinition>()
        for (definition in definitions) {
            //val localHref = definition.identifier.replace("http://legacy.api.gov.au", "")
            val localHref = definition.identifier.split("/").takeLast(2).joinToString("/")
            var shortDef = definition.definition
            if (shortDef.length > maxLength) {
                shortDef = shortDef.substring(0, maxLength) + " ..."
            }
            viewDefns.add(ViewDefinition(definition.name, definition.domain, shortDef, localHref, definition.status, definition.type))
        }
        model["definitions"] = viewDefns
    }

    private fun getFilterModel(search: Search): Filters {
        var ignoreSyn = search.getIgnoreSynonym(false)
        val filterDom = search.getDomainList(false)
        val domains = definitionService.getDomains()
        val l: MutableList<Domain> = arrayListOf()
        domains.forEach {
            if (filterDom.contains(it.acronym)) {
                l.add(it)
            }
        }
        return Filters(l, ignoreSyn)
    }

    @RequestMapping("/definitions/definition/{domain}/{id}")
    internal fun detail(model: MutableMap<String, Any>, @PathVariable domain: String, @PathVariable id: String): String {

        val identifier = """http://legacy.api.gov.au/definition/$domain/$id"""
        val definition = definitionService.getDefinition(identifier)

        model["name"] = definition.name
        model["domain"] = definition.domain
        model["status"] = definition.status
        model["definition"] = definition.definition
                .replace("\n", "<br/>")
                .replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;")
                .replace("  ", "&nbsp;&nbsp;")

        model["guidance"] = definition.guidance
                .replace("\n", "<br/>")
                .replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;")
                .replace("  ", "&nbsp;&nbsp;")

        model["identifier"] = definition.identifier
        model["href"] = definition.identifier.replace("http://legacy.api.gov.au", "/definitions")
        model["usage"] = definition.usage
        model["api"] = definition.identifier.replace("http://legacy.api.gov.au/definition", "/definitions/api/definition")
        if (definition.type != "") model["type"] = definition.type
        if (definition.sourceURL != "") model["source"] = definition.sourceURL
        model["typeValues"] = definition.values
        model["typeFacets"] = definition.facets

        val relations = relationshipService.getRelationships(definition.identifier)
        if (relations.isNotEmpty()) {
            model["relationShipImageUrl"] = model["api"].toString()
            //model["relationShipImageUrl"] = URLHelper().convertURL(request,model["api"].toString())
            val relationsWithDefinitions = addDefinitionToRelationshipResults(relations)
            model["relationships"] = relationsWithDefinitions
        }

        val syntaxes = syntaxService.getSyntax(identifier)
        if (syntaxes != null) {
            model["syntaxes"] = syntaxes.syntaxes
        }

        return "definitions-detail"
    }

    private fun addDefinitionToRelationshipResults(relations: Map<String, List<Relationship>>): MutableMap<String, MutableList<RelationshipWithDefinition>> {
        val relationsWithDefinitions = mutableMapOf<String, MutableList<RelationshipWithDefinition>>()
        for (relationName in relations.keys) {
            val definitions = mutableListOf<RelationshipWithDefinition>()
            for (result in relations[relationName]!!) {
                val definition = definitionService.getDefinition(result.to)
                var newURL = URLHelper().convertURL(request, result.to)
                newURL = "/definition" + Regex(".*definition").replace(newURL, "")
                val newResult = Relationship(result.meta, result.direction, newURL, definition.name)
                definitions.add(RelationshipWithDefinition(newResult, definition))
            }
            relationsWithDefinitions[relationName] = definitions
        }
        return relationsWithDefinitions
    }

    @RequestMapping("/definitions/synonyms")
    internal fun synonyms(model: MutableMap<String, Any>): String {
        model["synonyms"] = synonymService.getSynonyms()
        return "synonyms"
    }
}
