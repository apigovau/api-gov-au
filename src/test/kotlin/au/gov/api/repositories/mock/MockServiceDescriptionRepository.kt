package au.gov.api.repositories.mock

import au.gov.api.config.Config
import au.gov.api.repositories.IServiceDescriptionRepository
import au.gov.api.repositories.dto.IndexDTO
import au.gov.api.repositories.dto.ServiceDescriptionDTO
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
    private val indexes = ResourceCache(uriFetcher, 5, convert = { serial -> Klaxon().parse<IndexDTO>(serial)!! })
    private val descriptions = ResourceCache(uriFetcher, 5, convert = { serial -> Klaxon().parse<ServiceDescriptionDTO>(serial)!! })

    override val descriptionCache: ResourceCache<ServiceDescriptionDTO>
        get() = descriptions

    override val indexCache: ResourceCache<IndexDTO>
        get() = indexes

    override val pageProcessor: IPageProcessor
        get() = processor

    override val baseRepoUri: String
        get() = baseUri
}
