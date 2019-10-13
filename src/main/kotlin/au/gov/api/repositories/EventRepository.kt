package au.gov.api.repositories

import au.gov.api.config.Config
import au.gov.api.models.Event
import com.beust.klaxon.Klaxon
import org.springframework.stereotype.Component

@Component
class EventRepository : IEventRepository {

    override fun get(id: String): List<Event> {
        val logURL = Config.get("LogURI")+"list?name="+id
        val req = khttp.get(logURL, mapOf(),timeout = 1.0)
        val klaxon = Klaxon()
        return klaxon.parseArray(req.text)!!
    }
}