package guru.furu.kgaBackend.domain.nodes.incoming

import java.util.UUID

data class NewComment(
    val authorId: UUID,
    val onImageId: UUID,
    val text: String,
)
