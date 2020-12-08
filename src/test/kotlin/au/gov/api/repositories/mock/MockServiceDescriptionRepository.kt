package au.gov.api.repositories.mock

import au.gov.api.repositories.IServiceDescriptionRepository
import au.gov.api.repositories.dao.IndexDAO
import au.gov.api.repositories.dao.ServiceDescriptionDAO
import au.gov.api.repositories.processors.IPageProcessor
import au.gov.api.repositories.processors.mock.MockPageProcessor
import au.gov.api.web.ResourceCache
import au.gov.api.web.URIFetcher
import com.beust.klaxon.Klaxon
import org.springframework.stereotype.Component

@Component
class MockServiceDescriptionRepository constructor(baseRepoUri: String, uriFetcher: URIFetcher) : IServiceDescriptionRepository {

    private val processor = MockPageProcessor
    private val baseUri = baseRepoUri
    private val indexes = ResourceCache(uriFetcher, 5, convert = { serial -> Klaxon().parse<IndexDAO>(serial)!! })
    private val descriptions = ResourceCache(uriFetcher, 5, convert = { serial -> Klaxon().parse<ServiceDescriptionDAO>(serial)!! })

    override val descriptionCache: ResourceCache<ServiceDescriptionDAO>
        get() = descriptions

    override val indexCache: ResourceCache<IndexDAO>
        get() = indexes

    override val pageProcessor: IPageProcessor
        get() = processor

    override val baseRepoUri: String
        get() = baseUri
}
