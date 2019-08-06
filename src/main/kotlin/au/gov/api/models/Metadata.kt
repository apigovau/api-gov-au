package au.gov.api.asset

class Metadata(val id:String = "", val tags:List<String> = listOf()){
    fun matchesTags(inputTags:List<String>) = inputTags.intersect(tags).isNotEmpty()
    override fun toString() = id


}


