package au.gov.api.repositories.processors.mock

import au.gov.api.models.Definition
import au.gov.api.repositories.dao.DefinitionDAO
import au.gov.api.repositories.processors.IPageProcessor
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
        val testDefinitionString = Klaxon().toJsonString(DefinitionDAO(testDefinition))
        fetcher.map["https://legacy.api.gov.au/api/definition/edu/edu307"] = testDefinitionString

        ResourceCache(fetcher, 60, convert = { serial -> Klaxon().parse<DefinitionDAO>(serial)!! })
    }.invoke()
}