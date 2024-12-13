package guru.furu.kgaBackend.domain.nodes.outgoing

import guru.furu.kgaBackend.domain.nodes.Comment
import guru.furu.kgaBackend.domain.nodes.Tag
import kotlinx.datetime.Instant
import java.util.UUID

data class ImageDetails(
    val uploaderAccountId: UUID,
    val imageFilePath: String,
    val title: String,
    val uploadedAt: Instant,
    // TODO: add image metadata on save
    // val uploadMetadata: ImageMetadata(),
    val comments: List<Comment>,
    val tags: List<Tag>,
)
