package au.gov.api.web.test

import au.gov.api.repositories.dao.ServiceDescriptionDAO
import au.gov.api.web.ResourceCache
import au.gov.api.web.mock.MockURIFetcher
import com.beust.klaxon.Klaxon
import org.junit.Assert
import org.junit.Test

class TestCache {

    @Test
    fun can_get_deserialised_object_from_cache(){

        val fetcher = MockURIFetcher()
        val cache = ResourceCache(fetcher, 1, convert = { serial -> Klaxon().parse<ServiceDescriptionDAO>(serial)!! })

        val testDTO = ServiceDescriptionDAO("name", "description", listOf("a", "b"))
        val testDTOString = Klaxon().toJsonString(testDTO)

        fetcher.map["aurl"] = testDTOString

        val fetchedDTO = cache.get("aurl")

        Assert.assertEquals(fetchedDTO.description, testDTO.description)
        Assert.assertEquals(fetchedDTO.name, testDTO.name)
        Assert.assertEquals(fetchedDTO.pagesMarkdown, testDTO.pagesMarkdown)
    }

    @Test
    fun cache_will_return_expired_content_if_live_not_available(){

        val fetcher = MockURIFetcher()
        val cache = ResourceCache(fetcher, 1, convert = { serial -> Klaxon().parse<ServiceDescriptionDAO>(serial)!! })

        val testDTO = ServiceDescriptionDAO("name", "description", listOf("a", "b"))
        val testDTOString = Klaxon().toJsonString(testDTO)

        fetcher.map["aurl"] = testDTOString

        cache.get("aurl")

        cache.expire("aurl")
        Assert.assertEquals(ResourceCache.LastOperationStatus.MISS, cache.lastOperationStatus)

        fetcher.map.clear()


        val fetchedDTO = cache.get("aurl")
        Assert.assertEquals(ResourceCache.LastOperationStatus.EXPIRED, cache.lastOperationStatus)

        Assert.assertEquals(fetchedDTO.description, testDTO.description)
        Assert.assertEquals(fetchedDTO.name, testDTO.name)
        Assert.assertEquals(fetchedDTO.pagesMarkdown, testDTO.pagesMarkdown)
    }
}
