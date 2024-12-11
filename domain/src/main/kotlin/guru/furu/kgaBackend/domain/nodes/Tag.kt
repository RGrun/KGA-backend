package guru.furu.kgaBackend.domain.nodes

import java.util.UUID

data class Tag(
    override val id: UUID,
    val title: String,
) : Node
