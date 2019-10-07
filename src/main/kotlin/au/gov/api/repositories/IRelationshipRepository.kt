package au.gov.api.repositories

import au.gov.api.models.Relationship
import au.gov.api.models.RelationshipMetadata

interface IRelationshipRepository {
    fun getRelationships(identifier: String): Map<String, List<Relationship>>
    fun getRelationshipMetadata(relationType: String): RelationshipMetadata
}