package au.gov.dxa

import com.beust.klaxon.*
import org.springframework.stereotype.Component
import java.net.URL

data class ServiceDTO(val name:String = "", val description:String = "", val pages:List<String> = listOf(""))
data class ServiceListVM(val name:String, val definition:String, val domain:String, val status:String, val path:String)

@Component
class ServiceDescriptionRepository() {

    val baseRepoUri = System.getenv("BaseRepoURI")?: throw RuntimeException("No environment variable: BaseRepoURI")
    var descriptionCache = ResourceCache<ServiceDTO>(NaiveAPICaller(), 5, convert = { serial -> Klaxon().parse<ServiceDTO>(serial)!! })
    var indexCache = ResourceCache<IndexDTO>(NaiveAPICaller(), 5, convert = { serial -> Klaxon().parse<IndexDTO>(serial)!! })

    fun get(id:String) : ServiceDTO
    {
        try {
            val description = descriptionCache.get("$baseRepoUri/service/$id")
            return description
        }catch (e:Exception) {
            val serviceWithMatchingName = list().filter { it -> it.name.toLowerCase().replace(" ", "-") == id }.firstOrNull()
            if (serviceWithMatchingName == null) throw RuntimeException("Couldn't find service for id: $id")

            val idOfServiceWithMatchingName = serviceWithMatchingName.path
            val description = descriptionCache.get("$baseRepoUri/service/$idOfServiceWithMatchingName")
            return description
        }
    }


    data class IndexDTO(val content:List<IndexServiceDTO>)
    data class IndexServiceDTO(val id:String, val name:String, val description:String)
    fun list(): List<ServiceListVM>{

        val index = indexCache.get("$baseRepoUri/index")
        return index!!.content.map { it -> ServiceListVM(it.name, it.description, "Metadata", "Published", it.id) }
    }

}