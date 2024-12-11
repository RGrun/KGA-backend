package guru.furu.kgaBackend.client.dto.incoming

import kotlinx.serialization.Serializable

@Serializable
data class NewTagDTO(
    val title: String,
)
