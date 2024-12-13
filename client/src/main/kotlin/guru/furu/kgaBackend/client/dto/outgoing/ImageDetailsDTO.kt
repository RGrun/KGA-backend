package guru.furu.kgaBackend.client.dto.outgoing

import kotlinx.serialization.Serializable

@Serializable
data class ImageDetailsDTO(
    val uploaderAccountId: String,
    val imageFilePath: String,
    val title: String,
    val uploadedAt: Long,
    // TODO: add image metadata on save
    // val uploadMetadata: ImageMetadataDTO(),
    val comments: List<CommentDTO>,
    val tags: List<TagDTO>,
)
