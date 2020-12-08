package au.gov.api.repositories

import au.gov.api.models.Page
import au.gov.api.models.ServiceDescription
import au.gov.api.models.ServiceDescriptionListItem
import au.gov.api.repositories.dao.IndexDAO
import au.gov.api.repositories.dao.IndexServiceDAO
import au.gov.api.repositories.dao.ServiceDescriptionDAO
import au.gov.api.repositories.processors.IPageProcessor
import au.gov.api.web.ResourceCache

interface IServiceDescriptionRepository {

    val baseRepoUri : String
    val descriptionCache : ResourceCache<ServiceDescriptionDAO>
    val indexCache : ResourceCache<IndexDAO>
    val pageProcessor : IPageProcessor

    fun get(id:String) : ServiceDescription
    {
        val serviceDescriptionDTO = try {
            descriptionCache.get(baseRepoUri +"service/$id")
        } catch (e:Exception) {
            val serviceWithMatchingName = list().firstOrNull { it -> it.name.toLowerCase().replace(" ", "-") == id }
                    ?: throw RuntimeException("Couldn't find service for id: $id")

            val idOfServiceWithMatchingName = serviceWithMatchingName.path
            descriptionCache.get(baseRepoUri +"service/$idOfServiceWithMatchingName")
        }

        val pages = serviceDescriptionDTO.pagesMarkdown.map { pageProcessor.processPage(it) }
        val navigation = processNavigation(pages)

        return ServiceDescription(serviceDescriptionDTO.name, serviceDescriptionDTO.description, pages, navigation)
    }

    fun list(): List<ServiceDescriptionListItem>{
        val index = indexCache.get(baseRepoUri+"index")
        return processIndexList(index)
    }

    private fun processIndexList(inList: IndexDAO):List<ServiceDescriptionListItem>{
        val output:MutableList<ServiceDescriptionListItem> = mutableListOf()
        inList.content.forEach{output.add(processIndexItem(it))}
        return output
    }

    private fun processIndexItem(inItem: IndexServiceDAO):ServiceDescriptionListItem{
        val tagsCopy = inItem.tags.toMutableList()
        val name = inItem.name
        val description = inItem.description
        val domain = getTagItem(tagsCopy, "Category")
        val status = getTagItem(tagsCopy, "Status")
        val agency = getTagItem(tagsCopy, "AgencyAcr")
        val security = getTagItem(tagsCopy, "Security")
        val tech = getTagItem(tagsCopy, "Technology")
        val openAPISpec = getTagItem(tagsCopy, "OpenAPISpec")
        val logoURI = if (inItem.logoURI == "") "/img/NoLogo.png" else inItem.logoURI
        val metadata = inItem.metadata
        return  ServiceDescriptionListItem(name,description,domain,status,agency,security,tech,openAPISpec,inItem.id,tagsCopy,logoURI,metadata)
    }

    private fun getTagItem(inTags:MutableList<String>, tagToFind:String ):String{
        var foundVal = false
        var tagFoundIndex = 0
        var output = ""
        for(i in inTags.indices){
            if (inTags[i].contains(tagToFind))
            {
                output = inTags[i].replace("$tagToFind:","")
                foundVal = true
                break
            }
            tagFoundIndex++
        }
        if(foundVal){
            inTags.removeAt(tagFoundIndex)
        }
        return output
    }

    fun flush(id:String){
        descriptionCache.expire(baseRepoUri+"service/$id")
    }

    private fun processNavigation(pages: List<Page>): Map<String, List<String>>{
        val nav = mutableMapOf<String,List<String>>()
        pages.forEach { it -> nav[it.title] = it.subHeadings }
        return nav
    }
}
