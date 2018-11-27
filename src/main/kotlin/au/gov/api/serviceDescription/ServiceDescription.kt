package au.gov.api.serviceDescription

import com.beust.klaxon.Json


class ServiceDescription(val name:String = "", val description:String = "", @Json(name = "pages") val pagesMarkdown:List<String> = listOf("")) {

    @Json(ignored = true)
    val pages:List<Page>
    var navigation: Map<String,List<String>>


    init{
        pages = pagesMarkdown.map {it -> Page(it) }
        navigation = navigation()
    }

    private fun navigation(): Map<String, List<String>>{
        val nav = mutableMapOf<String,List<String>>()
        pages.forEach { it -> nav[it.title] = it.subHeaddings }
        return nav
    }

    fun next(page:Page):Page? = pages.getOrNull(pages.indexOf(page) + 1)
    fun previous(page:Page):Page? = pages.getOrNull(pages.indexOf(page) - 1)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceDescription

        if (name != other.name) return false
        if (description != other.description) return false
        if (pagesMarkdown != other.pagesMarkdown) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + pagesMarkdown.hashCode()
        return result
    }


}
