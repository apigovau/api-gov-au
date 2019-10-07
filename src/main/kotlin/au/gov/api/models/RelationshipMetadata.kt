package au.gov.api.models

data class RelationshipMetadata(val type: String, val directed: Boolean, val verbs: Map<Direction, String>)