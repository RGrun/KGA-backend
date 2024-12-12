package guru.furu.kgaBackend.domain.nodes

import kotlinx.datetime.Instant
import java.util.UUID

data class Image(
    override val nodeId: UUID,
    val uploaderId: UUID,
    val title: String,
    val description: String,
    val uploadedAt: Instant,
    val fileName: String,
) : Node
