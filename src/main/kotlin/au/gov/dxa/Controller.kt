package au.gov.dxa

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping


@Controller
class Controller {


    @Autowired
    lateinit var serviceDescriptionService:ServiceDescriptionService

    @RequestMapping("/")
    fun searchSubmit(model:MutableMap<String, Any?>): String{
        return "search"

    }

    @RequestMapping("/service/{id}")
    fun detail(@PathVariable id:String, model:MutableMap<String,Any?>): String{
        return detail_page(id, "", model)
    }



    @RequestMapping("/service/{id}/{title}")
    fun detail_page(@PathVariable id:String,@PathVariable title:String, model:MutableMap<String, Any?>): String{
        var serviceDescription = serviceDescriptionService.get(id)
        if(serviceDescription == null) return "detail"

        var page:ServiceDescriptionPage = serviceDescription.subpages[0]

        if(title != "") for(loopPage in serviceDescription!!.subpages) {
            if(title == loopPage.title) {
                page = loopPage
                break
            }
        }
        var rawContent = ""

        rawContent += pageWithHeading(page)
        if(page.subpages != null) for(subpage in page.subpages as List<ServiceDescriptionPage>) {
            rawContent += subpageWithHeading(subpage)
        }


        println(rawContent)
        val content = getMarkdown(rawContent)

        model.put("model", serviceDescription)
        model.put("content",content)
        return "detail"
    }

    private fun pageWithHeading(page:ServiceDescriptionPage): String {
        var output = "\n<a name=\"${page.title}\"></a>\n\n"
        output += "# ${page.title}\n\n${page.content}"
        return output
    }

    private fun subpageWithHeading(page:ServiceDescriptionPage): String {
        var output = "\n<a name=\"${page.title}\"></a>\n\n"
        output += "## ${page.title}\n\n${page.content}"
        return output
    }

    private fun getMarkdown(md:String):String{
        val options = MutableDataSet()
        val parser = Parser.builder(options).build()
        val renderer = HtmlRenderer.builder(options).build()

        // You can re-use parser and renderer instances
        val document = parser.parse(md.trimMargin())
        val html = renderer.render(document)  // "<p>This is <em>Sparta</em></p>\n"
        return html
    }


}
