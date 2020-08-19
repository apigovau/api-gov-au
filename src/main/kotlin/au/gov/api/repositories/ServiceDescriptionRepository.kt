package au.gov.api.repositories

import au.gov.api.config.Config
import au.gov.api.repositories.dao.IndexDAO
import au.gov.api.repositories.dao.ServiceDescriptionDAO
import au.gov.api.repositories.processors.IPageProcessor
import au.gov.api.repositories.processors.PageProcessor
import au.gov.api.web.NaiveAPICaller
import au.gov.api.web.ResourceCache
import com.beust.klaxon.Klaxon
import org.springframework.stereotype.Component

@Component
class ServiceDescriptionRepository : IServiceDescriptionRepository {

    private val processor = PageProcessor
    private val baseUri = Config.get("BaseRepoURI")
    private val indexes = ResourceCache(NaiveAPICaller(), 5, convert = { serial -> Klaxon().parse<IndexDAO>(serial)!! })
    private val descriptions = ResourceCache(NaiveAPICaller(), 5, convert = { serial -> Klaxon().parse<ServiceDescriptionDAO>(serial)!! })

    override val descriptionCache: ResourceCache<ServiceDescriptionDAO>
        get() = descriptions

    override val indexCache: ResourceCache<IndexDAO>
        get() = indexes

    override val pageProcessor: IPageProcessor
        get() = processor

    override val baseRepoUri: String
        get() = baseUri
}
