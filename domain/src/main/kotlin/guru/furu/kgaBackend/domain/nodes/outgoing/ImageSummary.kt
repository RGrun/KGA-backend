package guru.furu.kgaBackend.domain.nodes.outgoing

import kotlinx.datetime.Instant
import java.util.UUID

// this is the summary details of an image,
// usually for listing thumbnails on an account's page or on the main browse page.
// the path here should point to the thumbnail.
data class ImageSummary(
    val uploaderAccountId: UUID,
    val title: String,
    val filePath: String,
    val uploadedDate: Instant,
)
