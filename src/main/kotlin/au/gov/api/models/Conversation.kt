package au.gov.api.models

import com.beust.klaxon.Json

data class Conversation(val id:Int, val title: String, var body: String, val state: String, val typeTag: String, val tags: MutableList<String>, val mainUserName: String, val mainUserImageURI: String, val numComments: Int, val lastUpdated: String, @Json(ignored = true)var comments:MutableList<Comment> = mutableListOf())