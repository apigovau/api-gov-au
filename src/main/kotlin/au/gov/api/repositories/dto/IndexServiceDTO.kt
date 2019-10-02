package au.gov.api.repositories.dto

import au.gov.api.models.IndexMetadata

data class IndexServiceDTO(val id:String, val name:String, val description:String,  val tags:MutableList<String>, val logoURI:String, val metadata: IndexMetadata)