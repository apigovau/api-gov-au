package au.gov.api.repositories.processors

import au.gov.api.models.Definition
import au.gov.api.models.Page
import au.gov.api.repositories.dto.DefinitionDTO
import au.gov.api.web.NaiveAPICaller
import au.gov.api.web.ResourceCache
import com.beust.klaxon.Klaxon
import org.slf4j.LoggerFactory

object PageProcessor {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val definitionCache = ResourceCache<DefinitionDTO>(NaiveAPICaller(), 60, convert = { serial -> Klaxon().parse<DefinitionDTO>(serial)!! })

    fun processPage(markdown: String): Page {
        val title = processPageTitle(markdown)
        val subHeadings = processSubHeadings(markdown)
        val preProcessed = preProcessMarkdown(markdown)

        return Page(title, subHeadings, preProcessed)
    }

    private fun processPageTitle(markdown: String): String {
        val splitLines = markdown.lines()
        val title: String? = splitLines.find { it.startsWith("# ", true) } ?: return "Untitled"
        return title!!.replace("# ", "")
    }

    private fun processSubHeadings(markdown: String): MutableList<String> {
        val list = mutableListOf<String>()
        val splitLines = markdown.lines()
        val headings = splitLines.filter { it.startsWith("## ", true) }
        headings.forEach { it -> list.add(it.replace("## ", "")) }
        return list
    }

    private fun preProcessMarkdown(markdown: String): String {
        var processed = markdown

        processed = insertDefinitions(processed)

        return processed
    }

    private fun insertDefinitions(markdown: String): String {
        var output = markdown
        val regex = Regex("defcat```([^`\\n\\r])+```")
        val foundLinks = regex.findAll(markdown)
        for (item: MatchResult in foundLinks) {
            val regexAttribute = Regex("\\[(.*?)]")
            val foundAttribute = regexAttribute.findAll(item.value)

            val attribute = if (foundAttribute.count() > 0) foundAttribute.first().value else ""
            val definitionPath = item.value.split("```")[1].replace(attribute, "")
            val apiLinkEndpoint = "https://api.gov.au/api/definition/$definitionPath"
            val webEndpoint = "https://api.gov.au/definition/$definitionPath"

            output = try {
                val definitionVal = getAttribute(definitionCache.get(apiLinkEndpoint).content, attribute)
                if (attribute.replace("[", "").replace("]", "").split(';').last() == "1") {
                    output.replace(item.value, "[$definitionVal]($webEndpoint)")
                } else {
                    output.replace(item.value, definitionVal)
                }

            } catch (e: Exception) {
                log.warn("Couldn't resolve definition: $apiLinkEndpoint")
                output.replace(item.value, "```$definitionPath```")
            }
        }
        return output
    }

    private fun getAttribute(def: Definition, attr: String): String {
        return when (attr.replace("[", "").replace("]", "").split(';').first().toLowerCase()) {
            "name" -> def.name
            "domain" -> def.domain
            "status" -> def.status
            "definition" -> def.definition
            "guidance" -> def.guidance
            "identifier" -> def.identifier
            "type" -> def.type
            "sourceURL" -> def.sourceURL
            else -> def.name
        }
    }
}