package au.gov.api.repositories.processors

import au.gov.api.repositories.dao.DefinitionDAO
import au.gov.api.web.NaiveAPICaller
import au.gov.api.web.ResourceCache
import com.beust.klaxon.Klaxon
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object PageProcessor : IPageProcessor {

    override val log: Logger = LoggerFactory.getLogger(this.javaClass)
    override val definitionCache = ResourceCache<DefinitionDAO>(NaiveAPICaller(), 60, convert = { serial -> Klaxon().parse<DefinitionDAO>(serial)!! })
}