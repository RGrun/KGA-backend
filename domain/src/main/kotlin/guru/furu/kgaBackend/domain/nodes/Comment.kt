package guru.furu.kgaBackend.domain.nodes

import kotlinx.datetime.Instant
import java.util.UUID

data class Comment(
    override val id: UUID,
    val authorId: UUID,
    val onImageId: UUID,
    val text: String,
    val date: Instant,
) : Node
