package au.gov.api.models

data class Definition(val name: String = "", val domain: String ="", val status: String ="", val definition: String ="", val guidance: String ="", val identifier: String ="", val usage:List<String> = listOf(), val type: String ="", val values: List<String> = listOf(), val facets: Map<String, String> = mapOf(), val domainAcronym: String = "", val sourceURL:String = "")