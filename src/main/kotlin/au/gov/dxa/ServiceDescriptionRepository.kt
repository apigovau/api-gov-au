package au.gov.dxa

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.array
import com.beust.klaxon.string
import org.springframework.stereotype.Component


data class ServiceDescriptionPage(val title:String, val content:String, val subpages: List<ServiceDescriptionPage>?)
data class ServiceDescription(val name:String, val id:String, val subpages: List<ServiceDescriptionPage>)

@Component
class ServiceDescriptionRepository(mock:MutableList<String>? = null) {

    var serviceDescriptions = mutableMapOf<String,ServiceDescription>()

    init {
        var parsedObjects = mutableListOf<JsonObject>()

        if(mock == null) {
            for (serviceName in listOf("definitions-catalogue","superannuation-dashboard")) {
                var serviceJson: JsonObject = parse("/services/${serviceName}.json") as JsonObject
                parsedObjects.add(serviceJson)
            }
        }
        else{
            for(mockService in mock){
                var serviceJson: JsonObject = Parser().parse(StringBuilder().append(mockService)) as JsonObject
                parsedObjects.add(serviceJson)
            }
        }

        for(serviceJson in parsedObjects){
            var name = serviceJson.string("name")
            var id = name!!.toLowerCase().replace(" ","-")

            var pagesList = getSubPages(serviceJson)
            var service = ServiceDescription(name!!,id!!,pagesList.toList())
            serviceDescriptions.put(id, service)
        }
    }

    private fun getSubPages(thingWithPages: JsonObject): List<ServiceDescriptionPage> {
        var pages = thingWithPages.array<JsonObject>("subpages")
        var pagesList = mutableListOf<ServiceDescriptionPage>()
        for (page in pages!!) {
            var title = page.string("title")
            var markdown = page.string("markdown")
            if(page.containsKey("subpages")) {
                pagesList.add(ServiceDescriptionPage(title!!, markdown!!, getSubPages(page)))
            }else
            {
                pagesList.add(ServiceDescriptionPage(title!!, markdown!!, null))
            }
        }
        return pagesList.toList<ServiceDescriptionPage>()
    }

    private fun parse(name: String): Any? {
        val cls = Parser::class.java
        return cls.getResourceAsStream(name)?.let { inputStream ->
            try {
                return Parser().parse(inputStream)
            } catch(e:Exception){
                println("!!!!!!!!!!!!!!\n${e}")
                val position = e.message!!.replace("Unexpected character at position ","").split(":")[0].toInt()
                val inputAsString = cls.getResourceAsStream(name).bufferedReader().use { it.readText() }
                println(inputAsString.subSequence(position - 50,position + 50))
            }
        }
    }

    fun get(id:String):ServiceDescription?{
        if(id in serviceDescriptions) return serviceDescriptions[id]
        return null
    }

}