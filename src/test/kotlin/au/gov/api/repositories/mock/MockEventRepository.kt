package au.gov.api.repositories.mock

import au.gov.api.models.Event
import au.gov.api.repositories.IEventRepository
import au.gov.api.web.ResourceCache
import au.gov.api.web.URIFetcher
import com.beust.klaxon.Klaxon

class MockEventRepository constructor(uriFetcher: URIFetcher) : IEventRepository {

    private val events = ResourceCache(uriFetcher, 1, convert = { serial -> Klaxon().parseArray<Event>(serial)!! })

    override fun get(id: String): List<Event> = events.get(id)
}