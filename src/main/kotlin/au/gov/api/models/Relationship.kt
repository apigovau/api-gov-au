package au.gov.api.models

data class Relationship(var meta: RelationshipMetadata?, val direction: Direction, val to: String, var toName: String = "")