package au.gov.api.models

data class Comments(val content: MutableList<Comment>, val firstPage:Boolean, val lastPage:Boolean, val links:MutableList<Link>, val totalPages:Int)