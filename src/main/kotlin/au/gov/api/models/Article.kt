package au.gov.api.models

data class Article(val articleMetadata:ArticleMetadata = ArticleMetadata(), val title:String = "", val date:String = "", val summary:String = "", val markdown:String = "", var page:Page? = null)