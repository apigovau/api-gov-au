package au.gov.api.repositories.dao

import au.gov.api.models.IndexMetadata

data class IndexServiceDAO(val id:String, val name:String, val description:String, val tags:MutableList<String>, val logoURI:String, val metadata: IndexMetadata)