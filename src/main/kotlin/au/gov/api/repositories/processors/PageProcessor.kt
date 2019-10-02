package au.gov.api.repositories.processors

import au.gov.api.repositories.dto.DefinitionDTO
import au.gov.api.web.NaiveAPICaller
import au.gov.api.web.ResourceCache
import com.beust.klaxon.Klaxon
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object PageProcessor : IPageProcessor {

    override val log: Logger = LoggerFactory.getLogger(this.javaClass)
    override val definitionCache = ResourceCache<DefinitionDTO>(NaiveAPICaller(), 60, convert = { serial -> Klaxon().parse<DefinitionDTO>(serial)!! })
}