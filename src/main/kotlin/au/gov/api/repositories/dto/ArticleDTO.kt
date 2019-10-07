package au.gov.api.repositories.dto

data class ArticleDTO(val id: String, val tags: List<String>, val title:String = "", val date:String = "", val summary:String = "", val markdown:String = "")