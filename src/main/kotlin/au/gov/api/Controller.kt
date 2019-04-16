package au.gov.api

import au.gov.api.asset.AssetService
import au.gov.api.asset.Space
import au.gov.api.serviceDescription.ServiceDescriptionService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.net.URLDecoder


@Controller
class Controller {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    lateinit var assetService: AssetService


    @Autowired
    lateinit var serviceDescriptionService: ServiceDescriptionService

    @RequestMapping("/mvp")
    fun mvp(model:MutableMap<String, Any?>): String{
        return "mvp"

    }

    @RequestMapping("/")
    fun index(model:MutableMap<String, Any?>): String{
        return "landing"
    }

    @RequestMapping("/about")
    fun about(model:MutableMap<String, Any?>): String{
        return "about"
    }


    @RequestMapping("/community")
    fun community(model:MutableMap<String, Any?>): String{
        return "community"

    }

    @RequestMapping("/disclaimer")
    fun disclaimer(model:MutableMap<String, Any?>): String{
        return "disclaimer"

    }

    @RequestMapping("/firehose")
    fun services(model:MutableMap<String, Any?>): String{
        model.put("services", serviceDescriptionService.list())
        return "apis"

    }

    @RequestMapping("/service/{id}")
    fun detail(@PathVariable id:String, model:MutableMap<String,Any?>): String{

        try {
            return detail(id, "", model)
        }
        catch(e:Exception){
            log.warn(e.message)
        }
        return index(model)
    }

    @RequestMapping("/service/{id}/{title}")
    fun detail(@PathVariable id:String, @PathVariable title:String, model:MutableMap<String, Any?>): String{
        val serviceDescription = serviceDescriptionService.get(id) ?: return "detail"

        val unescapedTitle = URLDecoder.decode(title)
        val lastedit = serviceDescriptionService.getLastEdited(id)
        val page = serviceDescription.pages.firstOrNull {it -> it.title == unescapedTitle} ?: serviceDescription.pages.first()
        model.put("prevPage", serviceDescription.previous(page))
        model.put("nextPage", serviceDescription.next(page))
        model.put("currentPage", page.title)
        model.put("id", id)
        model.put("model", serviceDescription)
        model.put("pageList", serviceDescription.navigation)
        model.put("content", page.html())
        model.put("lastEdit", lastedit)
        return "detail"
    }


    @RequestMapping("/space/{space}")
    fun space(@PathVariable space:String, model:MutableMap<String, Any?>): String{
        val theSpace = assetService.getSpace(space)


        val parentSpaces = assetService.parentsOfSpace(space)

        val agencyLogoText = when(parentSpaces.size){
            0 -> theSpace.name
            else -> parentSpaces.map { assetService.getSpace(it).name.replace(" ","+") }.joinToString("&agency=")
        }
        model["agencyLogo"] = "https://api-gov-au-crest-branding.apps.y.cld.gov.au/inline.png?agency=${agencyLogoText}&height=200"

        model["space"] = theSpace
        model["articlesTagString"] = theSpace.tag
        val articles = assetService.getArticlesForTags(listOf(theSpace.tag))
        model["popularArticles"] = articles.take(2)
        model["articles"] = articles
        return "space"
    }

}
