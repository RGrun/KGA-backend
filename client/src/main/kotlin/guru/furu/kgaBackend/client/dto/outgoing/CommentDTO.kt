package guru.furu.kgaBackend.client.dto.outgoing

import kotlinx.serialization.Serializable

@Serializable
data class CommentDTO(
    val id: String,
    val text: String,
    val date: Long,
    // TODO Remove once we have a better system and replace with objects
    val authorId: String,
    val onImageId: String,
)
