package au.gov.web

import au.gov.api.web.ResourceCache
import au.gov.api.serviceDescription.Event
import com.beust.klaxon.Klaxon
import org.junit.Assert
import org.junit.Test

class EventTests {
    @Test
    fun can_deserialise_events(){

        var fetcher = MockURIFetcher()
        var cache = ResourceCache<Event>(fetcher, 1, convert = { serial -> Klaxon().parse<Event>(serial)!! })

        var testEvent = Event("Thu Jan 03 15:08:48 AEDT 2019","abc","Updated","Service","5b3gudq28d2ji12dd", "testing")
        var testDTOString = Klaxon().toJsonString(testEvent)


        fetcher.map["eventtest1"] = testDTOString

        var fetchedEvent = cache.get("eventtest1")

        Assert.assertEquals(fetchedEvent.timestamp, testEvent.timestamp)
        Assert.assertEquals(fetchedEvent.key, testEvent.key)
        Assert.assertEquals(fetchedEvent.action, testEvent.action)
        Assert.assertEquals(fetchedEvent.type, testEvent.type)
        Assert.assertEquals(fetchedEvent.name, testEvent.name)
        Assert.assertEquals(fetchedEvent.reason, testEvent.reason)
    }
}