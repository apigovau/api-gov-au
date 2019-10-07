package au.gov.api.repositories

import au.gov.api.config.Config
import au.gov.api.models.Direction
import au.gov.api.models.Relationship
import au.gov.api.models.RelationshipMetadata
import com.fasterxml.jackson.databind.ObjectMapper
import khttp.get
import org.springframework.stereotype.Component

@Component
class RelationshipRepository : IRelationshipRepository {

    private val baseRepoUri = Config.get("BaseRepoURI")

    override fun getRelationships(identifier: String): Map<String, List<Relationship>> {
        var response = get(baseRepoUri + "definitions/relationships?id=$identifier")
        var jsonHashMao = ObjectMapper().readValue(response.text, LinkedHashMap::class.java)
        var output: MutableMap<String, List<Relationship>> = mutableMapOf()
        for ((key, value) in jsonHashMao) {
            var res: MutableList<Relationship> = mutableListOf()
            for (resultEntry in (value as Iterable<LinkedHashMap<String, String>>)) {
                res.add(Relationship(getRelationshipMetadata(key as String), Direction.valueOf(resultEntry.getValue("direction")), resultEntry.getValue("to"), resultEntry.getValue("toName")))
            }
            output.set(key as String, res)
        }
        return output
    }

    override fun getRelationshipMetadata(relationType: String): RelationshipMetadata {
        var response = get(baseRepoUri + "definitions/relationships/meta?relationType=$relationType")
        var jsonHashMap = ObjectMapper().readValue(response.text, LinkedHashMap::class.java)
        var ver: MutableMap<Direction, String> = mutableMapOf()
        var verbs = (jsonHashMap["verbs"] as LinkedHashMap<String, String>)
        for (resultEntry in verbs.keys) {
            ver.set(Direction.valueOf(resultEntry), verbs[resultEntry]!!)
        }
        return RelationshipMetadata(jsonHashMap["type"] as String, (jsonHashMap["type"] as String).toBoolean(), ver.toMap())
    }
}