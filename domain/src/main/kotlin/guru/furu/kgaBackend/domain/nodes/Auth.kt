package guru.furu.kgaBackend.domain.nodes

import java.util.UUID

data class Auth(
    override val nodeId: UUID,
    val password: String,
) : Node
