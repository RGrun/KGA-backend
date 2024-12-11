package guru.furu.kgaBackend.adapter.db

import guru.furu.kgaBackend.adapter.model.RelationshipType
import guru.furu.kgaBackend.domain.nodes.Account
import guru.furu.kgaBackend.domain.nodes.Image
import guru.furu.kgaBackend.domain.nodes.Node
import java.util.UUID

interface DatabaseAccess {
    suspend fun saveNode(node: Node)

    suspend fun loadImageById(id: UUID): Image

    suspend fun createWithRelationship(
        from: Node,
        to: Node,
        type: RelationshipType,
    )

    suspend fun loadAccountById(id: UUID): Account

    suspend fun loadAccountByEmail(email: String): Account
}
