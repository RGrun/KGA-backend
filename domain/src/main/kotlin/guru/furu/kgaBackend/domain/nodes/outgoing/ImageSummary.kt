package guru.furu.kgaBackend.domain.nodes.outgoing

import kotlinx.datetime.Instant
import java.util.UUID

data class ImageSummary(
    val uploaderAccountId: UUID,
    val title: String,
    val filePath: String,
    val uploadedDate: Instant,
)
