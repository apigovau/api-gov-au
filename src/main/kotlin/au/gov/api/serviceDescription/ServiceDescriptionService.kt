package au.gov.api.serviceDescription

import au.gov.api.config.Config
import khttp.structures.authorization.BasicAuthorization
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import com.beust.klaxon.JsonObject
import java.io.StringReader
import java.lang.Exception

@Service
class ServiceDescriptionService {

    @Autowired
    lateinit var repo: ServiceDescriptionRepository

    fun get(id:String): ServiceDescription? {
        return repo.get(id)
    }

    fun getLastEdited(id:String):String {
        try {
            return "Last edited: " + getEvents(id).firstOrNull()!!.timestamp
        } catch (e:Exception)
        {
            return ""
        }
    }
    private fun getEvents(id:String) : List<Event> {
        val logURL = Config.get("LogURI")+"list?name="+id
        val req = khttp.get(logURL, mapOf(),timeout = 1.0)
        val klaxon = Klaxon()
        return klaxon.parseArray(req.text)!!
    }



    fun list(): List<ServiceListVM> = repo.list()

    fun flush(id:String) = repo.flush(id)
}
data class Event(var timestamp:String = "", var key:String = "", var action:String = "", var type:String = "", var name:String = "", var reason:String = "")

