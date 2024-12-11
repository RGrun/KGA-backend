package guru.furu.kgaBackend.client.dto.outgoing

import kotlinx.serialization.Serializable

@Serializable
data class TagDTO(
    val id: String,
    val title: String,
)
