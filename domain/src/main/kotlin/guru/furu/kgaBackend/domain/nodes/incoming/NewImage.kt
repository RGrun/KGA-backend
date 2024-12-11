package guru.furu.kgaBackend.domain.nodes.incoming

import java.util.UUID

data class NewImage(
    val uploaderAccountId: UUID,
    val title: String,
    val description: String,
)
