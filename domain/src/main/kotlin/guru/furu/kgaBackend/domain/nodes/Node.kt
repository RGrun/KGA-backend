package guru.furu.kgaBackend.domain.nodes

import java.util.UUID

sealed interface Node {
    val id: UUID
}
