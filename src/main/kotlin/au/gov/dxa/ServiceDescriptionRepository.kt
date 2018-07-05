package au.gov.dxa

import com.beust.klaxon.*
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import java.net.URL
import java.io.File


data class ServiceDescriptionPage(val title:String, val content:String, val subpages: List<ServiceDescriptionPage>?)
data class ServiceDTO(val id:String = "", val name:String = "", val description:String = "", val pages:List<String> = listOf(""))
data class ServiceListVM(val name:String, val definition:String, val domain:String, val status:String, val path:String)

@Component
class ServiceDescriptionRepository(mock:MutableList<String>? = null) {

    private var faqs = mutableMapOf<String,String>()

    init {
        addFaqs()
    }


    private fun addFaqs() {
        listOf("ato_sbr_declarations", "matching_ato_records").forEach { it -> faqs.put(it, read("/faqs/$it.md")) }
    }

    private fun getService(id:String) : ServiceDTO?
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

    fun get(id:String):ServiceDTO?{
        return getService(id)
    }



    fun getFaq(id:String):String?{
        if(id in faqs) return faqs[id]
        return null
    }

    fun list(): List<ServiceListVM>{

        val list = mutableListOf<ServiceListVM>()
        var returnString: String = getRESTReturnString()

        val splitdata = returnString.replace("[\"","").replace("\"]","").split("\",\"")

        for(serviceData in splitdata)
        {
            list.add(ServiceListVM(serviceData.split("/").last(), "blah ","Metadata", "Published", serviceData.split("/").last()))
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