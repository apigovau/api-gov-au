package au.gov.api.models

import com.vladsch.flexmark.ext.gfm.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet

class Page(val title: String, val subHeadings: MutableList<String>, val preProcessed: String) {
    fun html():String{
        val options = MutableDataSet()
        options.set(Parser.EXTENSIONS, listOf(TablesExtension.create()));
        options.set(HtmlRenderer.GENERATE_HEADER_ID, true)
        options.set(HtmlRenderer.RENDER_HEADER_ID, true)

        val parser = Parser.builder(options).build()
        val renderer = HtmlRenderer.builder(options).build()

        // You can re-use parser and renderer instances
        val document = parser.parse(preProcessed.trimMargin())
        return renderer.render(document)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Page

        if (title != other.title) return false
        if (subHeadings != other.subHeadings) return false
        if (preProcessed != other.preProcessed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + subHeadings.hashCode()
        result = 31 * result + preProcessed.hashCode()
        return result
    }
}