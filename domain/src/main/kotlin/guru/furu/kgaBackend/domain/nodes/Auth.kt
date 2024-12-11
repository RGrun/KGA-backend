package guru.furu.kgaBackend.domain.nodes

import java.util.UUID

data class Auth(
    override val id: UUID,
    val password: String,
) : Node
