package au.gov.api.asset

class Space(val tag:String = "" , val name:String = "", val overview:String = "", val childSpaces:List<String> = listOf(), val logoURL:String =""){
    fun popularArticles():List<Article> = listOf()
}

