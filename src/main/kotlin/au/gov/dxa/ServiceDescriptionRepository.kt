package au.gov.dxa

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.array
import com.beust.klaxon.string
import org.springframework.stereotype.Component


data class ServiceDescriptionPage(val title:String, val content:String, val subpages: List<ServiceDescriptionPage>?)
data class ServiceDescription(val name:String, val id:String, val subpages: List<ServiceDescriptionPage>, val config:Map<String,String> = mapOf())
data class ServiceListVM(val name:String, val definition:String, val domain:String, val status:String, val path:String)

@Component
class ServiceDescriptionRepository(mock:MutableList<String>? = null) {

    private var serviceDescriptions = mutableMapOf<String,ServiceDescription>()

    init {
        val parsedObjects = mutableListOf<JsonObject>()

        if(mock == null) {
            listOf("superannuation-dashboard.json", "definitions-catalogue.json").forEach { it -> parsedObjects.add( parse("/services/$it") as JsonObject) }

        }
        else{
            mock.forEach{ it -> parsedObjects.add(Parser().parse(StringBuilder().append(it)) as JsonObject)}
        }

        for(serviceJson in parsedObjects){
            val name = serviceJson.string("name")?:""
            val id = name.toLowerCase().replace(" ","-")
            val pagesList = getSubPages(serviceJson)
            var configString = serviceJson.string("configuration")?:"{}"
            if(configString == "") configString = "{}"
            val configMap = Parser().parse(StringBuilder().append(configString)) as Map<String, String>
            val service = ServiceDescription(name,id,pagesList.toList(), configMap)
            serviceDescriptions[id] = service
        }
    }

    private fun getSubPages(thingWithPages: JsonObject): List<ServiceDescriptionPage> {
        val pages = thingWithPages.array<JsonObject>("subpages")
        val pagesList = mutableListOf<ServiceDescriptionPage>()
        for (page in pages!!) {
            val title = page.string("title")
            val markdown = page.string("markdown")
            if(page.containsKey("subpages")) {
                pagesList.add(ServiceDescriptionPage(title!!, markdown!!, getSubPages(page)))
            }else
            {
                pagesList.add(ServiceDescriptionPage(title!!, markdown!!, null))
            }
        }
        return pagesList.toList()
    }

    private fun parse(name: String): Any? {
        val cls = Parser::class.java
        return cls.getResourceAsStream(name)?.let { inputStream ->
            try {
                return Parser().parse(inputStream)
            } catch(e:Exception){
                println("!!!!!!!!!!!!!!\n$e")
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

    fun list(): List<ServiceListVM>{

        val list = mutableListOf<ServiceListVM>()
        for(service in serviceDescriptions.values ){
            var description = service.config["description"]?: ""
            if(description.length > 200) description = description.substring(0, 200)+ " ..."
            list.add(ServiceListVM(service.name, description, "Metadata", "Published",service.name.replace(" ", "-").toLowerCase()))
        }
        return list.toList()

    }

}