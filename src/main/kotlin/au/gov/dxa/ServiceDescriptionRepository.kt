package au.gov.dxa

import com.beust.klaxon.*
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import java.net.URL

data class ServiceDTO(val id:String = "", val name:String = "", val description:String = "", val pages:List<String> = listOf(""))
data class ServiceListVM(val name:String, val definition:String, val domain:String, val status:String, val path:String)

@Component
class ServiceDescriptionRepository(mock:MutableList<String>? = null) {

    init {

    }

    private fun getService(id:String) : ServiceDTO
    {
        var returnString: String = getRESTReturnString("service",id)
        val serviceDTO = Klaxon().parse<ServiceDTO>(returnString) ?: ServiceDTO()
        return serviceDTO
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

    fun list(): List<ServiceListVM>{

        val list = mutableListOf<ServiceListVM>()
        var returnString: String = getRESTReturnString()

        val splitdata = returnString.replace("[\"","").replace("\"]","").split("\",\"")

        for(serviceData in splitdata)
        {
            //temp code until structure is final
            var serviceCuttent = getService(serviceData.split("/").last())
            list.add(ServiceListVM(serviceCuttent.name, serviceCuttent.description,"Metadata", "Published", serviceData.split("/").last()))
        }
        return list.toList()
    }

    private fun getRESTReturnString(endpoint : String = "index", endPointID:String = "") : String {
        var returnString: String = ""
        val url = System.getenv("BaseRepoURI") + endpoint + if (endpoint.last() == '/') endPointID else "/$endPointID"
        try {
            val con = URL(url).openConnection()
            con.readTimeout = 1000
            returnString = con.inputStream.bufferedReader().readText()
        } catch (e:Exception) {

        }
        return returnString
    }
}