package guru.furu.kgaBackend.adapter.model

import guru.furu.kgaBackend.domain.nodes.Node
import java.util.UUID

interface NodeSerializer {
    suspend fun serializeCreateNode(node: Node): Neo4jNodeSerializerImpl.QueryNamePair

    suspend fun serializeWithRelationship(
        from: Node,
        to: Node,
        relationshipType: RelationshipType,
    ): String

    suspend fun serializeMatchQuery(
        nodeName: String,
        matchClause: String,
    ): String

    suspend fun getNodeQueryById(id: UUID): String
}
