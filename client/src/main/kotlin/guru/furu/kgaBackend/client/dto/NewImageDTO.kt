package guru.furu.kgaBackend.client.dto

import kotlinx.serialization.Serializable

@Serializable
data class NewImageDTO(
    val uploaderAccountId: String,
    val title: String,
    val description: String,
)
