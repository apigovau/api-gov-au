package au.gov.dxa.serviceDescription

import au.gov.dxa.web.NaiveAPICaller
import au.gov.dxa.web.ResourceCache
import com.beust.klaxon.*
import org.springframework.stereotype.Component

data class ServiceListVM(val name:String, val definition:String, val domain:String, val status:String, val agency:String, val security:String, val technology:String, val openAPISpec:String, val path:String, val tags:MutableList<String>, val logoURI:String)

@Component
class ServiceDescriptionRepository() {

    val baseRepoUri = System.getenv("BaseRepoURI")?: throw RuntimeException("No environment variable: BaseRepoURI")
    var descriptionCache = ResourceCache<ServiceDescription>(NaiveAPICaller(), 5, convert = { serial -> Klaxon().parse<ServiceDescription>(serial)!! })
    var indexCache = ResourceCache<IndexDTO>(NaiveAPICaller(), 5, convert = { serial -> Klaxon().parse<IndexDTO>(serial)!! })

    fun get(id:String) : ServiceDescription
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
    data class IndexServiceDTO(val id:String, val name:String, val description:String,  val tags:MutableList<String>, val logoURI:String)
    fun list(): List<ServiceListVM>{

        val index = indexCache.get("$baseRepoUri/index")
        return processIndexList(index)

    }

    fun processIndexList(inList:IndexDTO):List<ServiceListVM>{
        var output:MutableList<ServiceListVM> = mutableListOf()
        inList.content.forEach{output.add(processIndexItem(it))}
        return output
    }
    fun processIndexItem(inItem:IndexServiceDTO):ServiceListVM{
        var tagsCopy = inItem.tags.toMutableList()
        val name = inItem.name
        val description = inItem.description
        val domain = replaceIfBlank(getTagItem(tagsCopy, "Category"),"Unknown")
        val status = replaceIfBlank(getTagItem(tagsCopy, "Status"), "Unknown")
        val agency = replaceIfBlank(getTagItem(tagsCopy, "AgencyAcr"), "Unknown")
        val security = replaceIfBlank(getTagItem(tagsCopy, "Security"), "Unknown")
        val tech = replaceIfBlank(getTagItem(tagsCopy, "Technology"), "Unknown")
        val openAPISpec = replaceIfBlank(getTagItem(tagsCopy, "OpenAPISpec"),"None")
        val logoURI = replaceIfBlank(inItem.logoURI, "/img/NoLogo.png")
        return  ServiceListVM(name,description,domain,status,agency,security,tech,openAPISpec,inItem.id,tagsCopy,logoURI)
    }

    fun replaceIfBlank(input:String, replaceWith:String):String{
        return if(input.trim() == "") replaceWith else input.trim()
    }

    fun getTagItem(intags:MutableList<String>, tagToFind:String ):String{
        var foundVal = false
        var tagFoundIndex = 0
        var output = ""
        for(i in intags.indices){
            if (intags[i].contains(tagToFind))
            {
                output = intags[i].replace("$tagToFind:","")
                foundVal = true
                break;
            }
            tagFoundIndex++
        }
        if(foundVal){
            intags.removeAt(tagFoundIndex)
        }
        return output
    }

    fun flush(id:String){
        descriptionCache.expire("$baseRepoUri/service/$id")
    }

}