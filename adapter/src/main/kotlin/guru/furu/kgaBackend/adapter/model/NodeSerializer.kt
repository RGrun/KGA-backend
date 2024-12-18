package guru.furu.kgaBackend.adapter.model

import guru.furu.kgaBackend.domain.nodes.Node
import java.util.UUID

interface NodeSerializer {
    suspend fun serializeUpsertNode(node: Node): Neo4jNodeSerializerImpl.QueryNamePair

    suspend fun serializeWithRelationship(
        from: Node,
        to: List<Node>,
        relationshipType: RelationshipType,
    ): String

    suspend fun serializeMatchQuery(
        nodeName: String,
        matchClause: String,
    ): String

    suspend fun getNodeQueryById(nodeId: UUID): String

    suspend fun serializeGetConnectedComponents(
        nodeId: UUID,
        relationshipType: RelationshipType,
        reverse: Boolean = false,
    ): String
}
