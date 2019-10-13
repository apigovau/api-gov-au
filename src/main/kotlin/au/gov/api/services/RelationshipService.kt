package au.gov.api.services

import au.gov.api.models.Relationship
import au.gov.api.models.RelationshipMetadata
import au.gov.api.repositories.IRelationshipRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RelationshipService @Autowired constructor(private val repo: IRelationshipRepository) {

    fun getRelationships(identifier: String): Map<String, List<Relationship>> = repo.getRelationships(identifier)

    fun getRelationshipMetadata(relationType: String): RelationshipMetadata = repo.getRelationshipMetadata(relationType)
}