package guru.furu.kgaBackend.api.routes

import guru.furu.kgaBackend.adapter.fs.ImagesFilesystemAccess
import guru.furu.kgaBackend.adapter.nodeaccess.AccountAccess
import guru.furu.kgaBackend.adapter.nodeaccess.ImagesAccess
import guru.furu.kgaBackend.adapter.toDomain
import guru.furu.kgaBackend.client.dto.incoming.NewImageDTO
import guru.furu.kgaBackend.client.dto.incoming.NewTagDTO
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.Application
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream
import java.util.Locale
import java.util.UUID

fun Application.imageManagementRoutes(
    imagesAccess: ImagesAccess,
    accountAccess: AccountAccess,
    imagesFilesystemAccess: ImagesFilesystemAccess,
    imageUploadMetadataFieldName: String = "uploadMetadata",
    imageUploadTagsFieldName: String = "tags",
) {
    routing {
        route("/images") {
            // TODO support adding tags during upload
            post("/upload") {
                // retrieve all multipart data (suspending)
                val multipart = call.receiveMultipart()
                val outputStream = ByteArrayOutputStream()
                var fileName: String? = null
                var newImageDTO: NewImageDTO? = null
                var tagsDTO: List<NewTagDTO>? = null
                multipart.forEachPart { part ->
                    // if part is a file (could be form item)
                    if (part is PartData.FileItem) {
                        // retrieve file name of upload
                        fileName = part.originalFileName!!

                        // use InputStream from part to save file
                        part.provider().toInputStream().use { inputStream ->
                            // copy the stream to the file with buffering
                            outputStream.buffered().use {
                                // note that this is blocking
                                inputStream.copyTo(it)
                            }
                        }
                    }

                    if (part is PartData.FormItem) {
                        if (part.name == imageUploadMetadataFieldName) {
                            newImageDTO = Json.decodeFromString<NewImageDTO>(part.value)
                        } else if (part.name == imageUploadTagsFieldName) {
                            tagsDTO = Json.decodeFromString<List<NewTagDTO>>(part.value)
                        }
                    }
                    // make sure to dispose of the part after use to prevent leaks
                    part.dispose()
                }

                val fileNameNotNull =
                    requireNotNull(fileName) {
                        "File name not provided!"
                    }

                val newImage = newImageDTO?.toDomain() ?: error("Could not parse new image data!")

                if (accountAccess.loadAccountById(newImage.uploaderAccountId) == null) {
                    call.respond(HttpStatusCode.BadRequest, "Specified uploader account could not be found.")
                    return@post
                }

                imagesAccess.recordNewImage(fileNameNotNull, newImage, tagsDTO)

                imagesFilesystemAccess.saveNewImage(
                    fileBytes = outputStream.toByteArray(),
                    fileName = fileNameNotNull,
                    newImage = newImage,
                )

                call.respond(HttpStatusCode.Created)
            }

            get("/load/uploader-id/{uploaderId}/file-name/{fileName}") {
                val uploaderId =
                    call.parameters["uploaderId"]
                        .let { UUID.fromString(it) }
                        ?: error("Could not parse uploaderId")
                val filename = call.parameters["fileName"] ?: error("Could not parse fileName")

                val isThumb =
                    call.request.queryParameters["thumb"]
                        ?.let { it.lowercase(Locale.getDefault()) == "true" }
                        ?: false

                val file = imagesFilesystemAccess.loadImage(uploaderId, filename, isThumb)
                if (file.exists()) {
                    call.respondFile(file)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}
