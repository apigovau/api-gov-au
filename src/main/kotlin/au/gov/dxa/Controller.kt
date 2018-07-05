package au.gov.dxa

import com.vladsch.flexmark.ext.gfm.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*


@Controller
class Controller {


    @Autowired
    lateinit var serviceDescriptionService:ServiceDescriptionService

    @RequestMapping("/mvp")
    fun mvp(model:MutableMap<String, Any?>): String{
        return "mvp"

    }

    @RequestMapping("/")
    fun searchSubmit(model:MutableMap<String, Any?>): String{
        model.put("services", serviceDescriptionService.list())
        return "search"

    }

    @RequestMapping("/service/{id}")
    fun detail(@PathVariable id:String, model:MutableMap<String,Any?>): String{
        return detailPage(id, "", model)
    }

    @RequestMapping("/service/{id}/{title}")
    fun detailPage(@PathVariable id:String,@PathVariable title:String, model:MutableMap<String, Any?>): String{
        val serviceDescription = serviceDescriptionService.get(id)
        return _detailPage(serviceDescription, title, model)
    }

    @RequestMapping("/faq/{id}")
    fun detailFaq(@PathVariable id:String, model:MutableMap<String,Any?>): String{

        var faq = serviceDescriptionService.getFaq(id)!!
        val content = getMarkdown(faq)
        model.put("content", content)
        return "faq"
    }

    private fun _detailPage(serviceDescription:ServiceDTO?, title: String, model: MutableMap<String, Any?>): String {
        if (serviceDescription == null) return "detail"
        var content:String = ""
        var pageTitle:String = title
        var pages: LinkedHashMap<String, String> = LinkedHashMap()
        for (page in serviceDescription.pages)
        {
            var pageMarkdown = getMarkdown(page)
            var pageTitle = getPageTitleFromMarkdown(page)
            pages.put(pageTitle, pageMarkdown)
        }

        for (page in pages)
        {
            page.value
        }

        if (pageTitle=="")
        {
            pageTitle = pages.keys.first()
        }

        content = pages.getValue(pageTitle)

        val lastPage = pageTitle == pages.keys.last()
        val firstPage = pageTitle == pages.keys.first()
        val currPageInded = pages.keys.indexOf(pageTitle)

        if (!lastPage) {
            model.put("nextPage", pages.keys.elementAt(currPageInded+1))
        }

        if (!firstPage) {
            model.put("prevPage", pages.keys.elementAt(currPageInded-1))
        }



        model.put("currentPage", pageTitle)
        model.put("model", serviceDescription)
        model.put("pageList", pages)
        model.put("content", content)

        return "detail"
    }

    private fun getPageData(page: ServiceDescriptionPage, indent:Int): String {
        var rawContent = ""
        rawContent += pageWithHeading(page, indent)
        if (page.subpages != null) for (subpage in page.subpages) {
            rawContent += getPageData(subpage, indent + 1)
        }
        return rawContent
    }

    private fun pageWithHeading(page:ServiceDescriptionPage, indent:Int): String {
        var output = "\n<a name=\"${page.title}\"></a>\n\n"
        output += "${"#".repeat(indent)} ${page.title}\n\n${page.content}"
        return output
    }

    private fun getMarkdown(md:String):String{
        val options = MutableDataSet()
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));
        val parser = Parser.builder(options).build()
        val renderer = HtmlRenderer.builder(options).build()

        // You can re-use parser and renderer instances
        val document = parser.parse(md.trimMargin())
        val html = renderer.render(document)  // "<p>This is <em>Sparta</em></p>\n"
        return html
    }

    private fun getPageTitleFromMarkdown(md:String):String
    {
        var splitLines = md.lines()
        var title = splitLines.find { it.startsWith("# ",true) }
        return title!!.replace("# ","") ?: "No title defined"
    }


}
