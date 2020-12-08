package au.gov.api.repositories.dao

import com.beust.klaxon.Json

data class ServiceDescriptionDAO(val name:String = "", val description:String = "", @Json(name = "pages") val pagesMarkdown:List<String> = listOf(""))