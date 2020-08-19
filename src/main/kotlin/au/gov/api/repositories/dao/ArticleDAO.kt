package au.gov.api.repositories.dao

data class ArticleDAO(val id: String, val tags: List<String>, val title:String = "", val date:String = "", val summary:String = "", val markdown:String = "")