package au.gov.dxa.serviceDescription

import com.vladsch.flexmark.ext.gfm.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet
import java.util.*

class Page(val markdown:String) {

    val title = title()
    val subHeaddings = subHeadings()
    val html = html()

    private fun title():String {
        var splitLines = markdown.lines()
        var title = splitLines.find { it.startsWith("# ",true) }
        if(title == null) return "Untitled"
        return title.replace("# ","")
    }

    private fun subHeadings():MutableList<String> {
        val list = mutableListOf<String>()
        var splitLines = markdown.lines()
        var headings = splitLines.filter { it.startsWith("## ",true) }
        headings.forEach { it -> list.add(it.replace("## ","")) }
        return list
    }

    private fun html():String{
        val options = MutableDataSet()
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));
        options.set(HtmlRenderer.GENERATE_HEADER_ID, true)
        options.set(HtmlRenderer.RENDER_HEADER_ID, true)

        val parser = Parser.builder(options).build()
        val renderer = HtmlRenderer.builder(options).build()

        // You can re-use parser and renderer instances
        val document = parser.parse(markdown.trimMargin())
        val html = renderer.render(document)
        return html
    }

    override fun toString() = title
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Page

        if (markdown != other.markdown) return false

        return true
    }

    override fun hashCode(): Int {
        return markdown.hashCode()
    }


}