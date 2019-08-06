package au.gov.api.models

data class ServiceListItem(val name:String, val definition:String, val domain:String, val status:String, val agency:String, val security:String, val technology:String, val openAPISpec:String, val path:String, val tags:MutableList<String>, val logoURI:String, val metadata:IndexMetadata)