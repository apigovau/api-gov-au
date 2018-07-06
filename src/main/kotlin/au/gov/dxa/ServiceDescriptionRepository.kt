package au.gov.dxa

import com.beust.klaxon.*
import org.springframework.stereotype.Component
import java.net.URL

data class ServiceDTO(val name:String = "", val description:String = "", val pages:List<String> = listOf(""))
data class ServiceListVM(val name:String, val definition:String, val domain:String, val status:String, val path:String)

@Component
class ServiceDescriptionRepository(mock:MutableList<String>? = null) {

    init {

    }

    private fun getService(id:String) : ServiceDTO
    {
        var returnString: String = getRESTReturnString("service",id)
        return Klaxon().parse<ServiceDTO>(returnString) ?: ServiceDTO()
    }

    private fun read(name:String):String{
        val cls = Parser::class.java
        val input = cls.getResourceAsStream(name)
        val inputAsString = input.bufferedReader().use { it.readText() }
        return inputAsString
    }

    fun get(id:String):ServiceDTO?{
        return getService(id)
    }

    data class IndexDTO(val content:List<IndexServiceDTO>)
    data class IndexServiceDTO(val id:String, val name:String, val description:String)
    fun list(): List<ServiceListVM>{

        var returnString: String = getRESTReturnString()
        val index = Klaxon().parse<IndexDTO>(returnString)

        return index!!.content.map { it -> ServiceListVM(it.name, it.description, "Metadata", "Published", it.id) }
    }

    private fun getRESTReturnString(endpoint : String = "index", endPointID:String = "") : String {
        var returnString: String = ""
        val baseRepoUri = System.getenv("BaseRepoURI")?: throw RuntimeException("No environment variable: BaseRepoURI")


        val url = baseRepoUri + endpoint + if (endpoint.last() == '/') endPointID else "/$endPointID"
        try {
            val con = URL(url).openConnection()
            con.readTimeout = 1000
            returnString = con.inputStream.bufferedReader().readText()
        } catch (e:Exception) {

        }
        return returnString
    }
}