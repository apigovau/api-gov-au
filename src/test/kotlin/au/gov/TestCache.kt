package au.gov

import au.gov.dxa.ResourceCache
import au.gov.dxa.ServiceDTO
import com.beust.klaxon.Klaxon
import org.junit.Assert
import org.junit.Test

class TestCache {
    @Test
    fun can_get_deserialised_object_from_cache(){

        var fetcher = MockURIFetcher()
        var cache = ResourceCache<ServiceDTO>(fetcher, 1, convert = { serial -> Klaxon().parse<ServiceDTO>(serial)!! })

        var testDTO = ServiceDTO("name","description",listOf("a","b"))
        var testDTOString = Klaxon().toJsonString(testDTO)

        fetcher.map["aurl"] = testDTOString

        var fetchedDTO = cache.get("aurl")

        Assert.assertEquals(fetchedDTO.description, testDTO.description)
        Assert.assertEquals(fetchedDTO.name, testDTO.name)
        Assert.assertEquals(fetchedDTO.pages, testDTO.pages)
    }

    @Test
    fun cache_will_return_expired_content_if_live_not_available(){

        var fetcher = MockURIFetcher()
        var cache = ResourceCache<ServiceDTO>(fetcher, 1, convert = { serial -> Klaxon().parse<ServiceDTO>(serial)!! })

        var testDTO = ServiceDTO("name","description",listOf("a","b"))
        var testDTOString = Klaxon().toJsonString(testDTO)

        fetcher.map["aurl"] = testDTOString

        cache.get("aurl")

        cache.expire("aurl")
        Assert.assertEquals(ResourceCache.LastOperationStatus.MISS, cache.lastOperationStatus)

        fetcher.map.clear()


        var fetchedDTO = cache.get("aurl")
        Assert.assertEquals(ResourceCache.LastOperationStatus.EXPIRED, cache.lastOperationStatus)

        Assert.assertEquals(fetchedDTO.description, testDTO.description)
        Assert.assertEquals(fetchedDTO.name, testDTO.name)
        Assert.assertEquals(fetchedDTO.pages, testDTO.pages)

    }

}