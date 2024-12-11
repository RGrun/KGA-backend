package guru.furu.kgaBackend.adapter.nodeaccess

import guru.furu.kgaBackend.domain.nodes.Image
import guru.furu.kgaBackend.domain.nodes.incoming.NewImage
import java.util.UUID

interface ImagesAccess {
    suspend fun recordNewImage(newImage: NewImage)

    suspend fun fetchImageInformation(imageId: UUID): Image
}
