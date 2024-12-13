package guru.furu.kgaBackend.client.dto.outgoing

import kotlinx.serialization.Serializable

@Serializable
data class ImageSummaryDTO(
    val uploaderAccountId: String,
    val title: String,
    val filePath: String,
    val uploadedDate: Long,
)
