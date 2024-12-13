package guru.furu.kgaBackend.adapter.nodeaccess

import guru.furu.kgaBackend.client.dto.incoming.NewTagDTO
import guru.furu.kgaBackend.domain.nodes.incoming.NewImage
import guru.furu.kgaBackend.domain.nodes.outgoing.ImageDetails
import guru.furu.kgaBackend.domain.nodes.outgoing.ImageSummary
import java.util.UUID

interface ImagesAccess {
    suspend fun recordNewImage(
        fileName: String,
        newImage: NewImage,
        tags: List<NewTagDTO>?,
    )

    suspend fun fetchImageDetails(imageId: UUID): ImageDetails?

    suspend fun fetchImagesForAccount(accountId: UUID): List<ImageSummary>
}
