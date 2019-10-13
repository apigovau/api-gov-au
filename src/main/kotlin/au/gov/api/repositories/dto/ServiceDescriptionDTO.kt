package au.gov.api.repositories.dto

import com.beust.klaxon.Json

data class ServiceDescriptionDTO(val name:String = "", val description:String = "", @Json(name = "pages") val pagesMarkdown:List<String> = listOf(""))