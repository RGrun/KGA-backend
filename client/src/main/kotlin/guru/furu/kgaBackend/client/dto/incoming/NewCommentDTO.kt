package guru.furu.kgaBackend.client.dto.incoming

import kotlinx.serialization.Serializable

@Serializable
data class NewCommentDTO(
    val authorId: String,
    val onImageId: String,
    val text: String,
)
