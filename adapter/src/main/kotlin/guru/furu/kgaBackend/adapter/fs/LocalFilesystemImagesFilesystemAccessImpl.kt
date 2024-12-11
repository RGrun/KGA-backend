package guru.furu.kgaBackend.adapter.fs

import guru.furu.kgaBackend.domain.nodes.incoming.NewImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.coobird.thumbnailator.Thumbnails
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths

class LocalFilesystemImagesFilesystemAccessImpl(
    private val localRoot: String,
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
            Files.createDirectories(Paths.get(fileNameImg))
            FileOutputStream(fullFilePath).use {
                it.write(fileBytes)
            }

            if (!skipThumbnailUpload) {
                val thumbPath = pathImgRoot + "thumb/"
                val thumbFilePath = "$thumbPath/$fileName"
                val outputStream = ByteArrayOutputStream()
                Thumbnails.of(File(fullFilePath))
                    .size(640, 480)
                    .toOutputStream(outputStream)

                Files.createDirectories(Paths.get(thumbPath))

                FileOutputStream(thumbFilePath).use {
                    it.write(outputStream.toByteArray())
                }
            }
        }
    }

    override suspend fun loadImage(
        fileName: String,
        isThumb: Boolean,
    ): File {
        TODO("Not yet implemented")
    }
}
