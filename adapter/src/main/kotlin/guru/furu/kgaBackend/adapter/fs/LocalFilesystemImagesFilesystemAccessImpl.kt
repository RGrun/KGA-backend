package guru.furu.kgaBackend.adapter.fs

import guru.furu.kgaBackend.domain.nodes.incoming.NewImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.coobird.thumbnailator.Thumbnails
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

class LocalFilesystemImagesFilesystemAccessImpl(
    private val localRoot: String,
    private val thumbnailWidth: Int = 160,
    private val thumbnailHeight: Int = 120,
) : ImagesFilesystemAccess {
    override suspend fun saveNewImage(
        fileBytes: ByteArray,
        fileName: String,
        newImage: NewImage,
        skipThumbnailUpload: Boolean,
    ) {
        val pathImgRoot = "$localRoot/${newImage.uploaderAccountId}/"

        withContext(Dispatchers.IO) {
            val fileNameImg = pathImgRoot + "img/"
            val fullFilePath = fileNameImg + fileName

            if (File(fullFilePath).exists()) {
                error("file $fileName already exists for this account!")
            }

            Files.createDirectories(Paths.get(fileNameImg))
            FileOutputStream(fullFilePath).use {
                it.write(fileBytes)
            }

            if (!skipThumbnailUpload) {
                val thumbPath = pathImgRoot + "thumb/"
                val thumbFilePath = "$thumbPath/$fileName"
                val outputStream = ByteArrayOutputStream()

                // TODO: this library can't handle webp files, need an alternative for them
                Thumbnails.of(File(fullFilePath))
                    .size(thumbnailWidth, thumbnailHeight)
                    .toOutputStream(outputStream)

                Files.createDirectories(Paths.get(thumbPath))

                FileOutputStream(thumbFilePath).use {
                    it.write(outputStream.toByteArray())
                }
            }
        }
    }

    override suspend fun loadImage(
        uploaderAccountId: UUID,
        fileName: String,
        isThumb: Boolean,
    ): File {
        val path =
            if (isThumb) {
                "$localRoot/$uploaderAccountId/thumb/$fileName"
            } else {
                "$localRoot/$uploaderAccountId/img/$fileName"
            }

        val file = File(path)

        if (!file.exists()) {
            throw FileNotFoundException()
        }

        return File(path)
    }
}
