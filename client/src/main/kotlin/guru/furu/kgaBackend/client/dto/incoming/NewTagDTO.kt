package guru.furu.kgaBackend.client.dto.incoming

import kotlinx.serialization.Serializable

@Serializable
data class NewTagDTO(
    // null/absent indicates new tag
    val nodeId: String?,
    val title: String,
)
