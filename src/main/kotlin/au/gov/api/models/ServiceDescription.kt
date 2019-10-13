package au.gov.api.models

class ServiceDescription(val name:String, val description:String, val pages:List<Page>, val navigation: Map<String,List<String>>) {

    fun next(page:Page):Page? = pages.getOrNull(pages.indexOf(page) + 1)

    fun previous(page:Page):Page? = pages.getOrNull(pages.indexOf(page) - 1)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceDescription

        if (name != other.name) return false
        if (description != other.description) return false
        if (pages != other.pages) return false
        if (navigation != other.navigation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + pages.hashCode()
        result = 31 * result + navigation.hashCode()
        return result
    }
}