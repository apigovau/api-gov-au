package au.gov.dxa

import au.gov.dxa.serviceDescription.ServiceDescriptionService
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
    lateinit var serviceDescriptionService: ServiceDescriptionService

    @RequestMapping("/mvp")
    fun mvp(model:MutableMap<String, Any?>): String{
        return "mvp"

    }

    @RequestMapping("/")
    fun index(model:MutableMap<String, Any?>): String{
        return "about"

    }

    @RequestMapping("/community")
    fun community(model:MutableMap<String, Any?>): String{
        return "community"

    }

    @RequestMapping("/apis")
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

        val page = serviceDescription.pages.firstOrNull {it -> it.title == unescapedTitle} ?: serviceDescription.pages.first()
        model.put("prevPage", serviceDescription.previous(page))
        model.put("nextPage", serviceDescription.next(page))
        model.put("currentPage", page.title)
        model.put("id", id)
        model.put("model", serviceDescription)
        model.put("pageList", serviceDescription.navigation)
        model.put("content", page.html())

        return "detail"
    }

}
