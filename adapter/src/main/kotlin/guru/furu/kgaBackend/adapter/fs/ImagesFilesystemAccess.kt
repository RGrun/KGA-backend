package guru.furu.kgaBackend.adapter.fs

import guru.furu.kgaBackend.domain.nodes.incoming.NewImage
import java.io.File

interface ImagesFilesystemAccess {
    suspend fun saveNewImage(
        fileBytes: ByteArray,
        fileName: String,
        newImage: NewImage,
        skipThumbnailUpload: Boolean = false,
    )

    suspend fun loadImage(
        fileName: String,
        isThumb: Boolean = false,
    ): File
}
