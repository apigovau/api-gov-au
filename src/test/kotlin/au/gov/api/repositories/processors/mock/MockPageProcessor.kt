package au.gov.api.repositories.processors.mock

import au.gov.api.models.Definition
import au.gov.api.models.Page
import au.gov.api.repositories.dto.DefinitionDTO
import au.gov.api.repositories.processors.IPageProcessor
import au.gov.api.repositories.processors.PageProcessor
import au.gov.api.web.NaiveAPICaller
import au.gov.api.web.ResourceCache
import au.gov.api.web.mock.MockURIFetcher
import com.beust.klaxon.Klaxon
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object MockPageProcessor : IPageProcessor {
    override val log: Logger = LoggerFactory.getLogger(this.javaClass)
    override val definitionCache = {
        val fetcher = MockURIFetcher()
        val testDefinition = Definition(name = "Course Code", status = "Standard")
        val testDefinitionString = Klaxon().toJsonString(DefinitionDTO(testDefinition))
        fetcher.map["https://api.gov.au/api/definition/edu/edu307"] = testDefinitionString

        ResourceCache(fetcher, 60, convert = { serial -> Klaxon().parse<DefinitionDTO>(serial)!! })
    }.invoke()
}