package guru.furu.kgaBackend.adapter.fs

import guru.furu.kgaBackend.domain.nodes.incoming.NewImage
import java.io.File
import java.util.UUID

interface ImagesFilesystemAccess {
    suspend fun saveNewImage(
        fileBytes: ByteArray,
        fileName: String,
        newImage: NewImage,
        skipThumbnailUpload: Boolean = false,
    )

    suspend fun loadImage(
        uploaderAccountId: UUID,
        fileName: String,
        isThumb: Boolean = false,
    ): File
}
