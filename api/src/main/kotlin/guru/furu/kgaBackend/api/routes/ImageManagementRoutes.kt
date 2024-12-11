package guru.furu.kgaBackend.api.routes

import guru.furu.kgaBackend.adapter.fs.ImagesFilesystemAccess
import guru.furu.kgaBackend.adapter.nodeaccess.ImagesAccess
import guru.furu.kgaBackend.adapter.toDomain
import guru.furu.kgaBackend.client.dto.NewImageDTO
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.Application
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import java.io.ByteArrayOutputStream

fun Application.imageManagementRoutes(
    imagesAccess: ImagesAccess,
    imagesFilesystemAccess: ImagesFilesystemAccess,
) {
    routing {
        route("/images") {
            post("/upload") {
                // retrieve all multipart data (suspending)
                val multipart = call.receiveMultipart()
                val outputStream = ByteArrayOutputStream()
                var fileName: String? = null
                var newImageDTO: NewImageDTO? = null
                multipart.forEachPart { part ->
                    // if part is a file (could be form item)
                    if (part is PartData.FileItem) {
                        // retrieve file name of upload
                        fileName = part.originalFileName!!

                        // use InputStream from part to save file
                        part.streamProvider().use { inputStream ->
                            // copy the stream to the file with buffering
                            outputStream.buffered().use {
                                // note that this is blocking
                                inputStream.copyTo(it)
                            }
                        }
                    }

                    if (part is PartData.FormItem) {
                        newImageDTO = Json.decodeFromString<NewImageDTO>(part.value)
                    }
                    // make sure to dispose of the part after use to prevent leaks
                    part.dispose()
                }

                val newImage = newImageDTO?.toDomain() ?: error("Could not parse new image data!")

                imagesFilesystemAccess.saveNewImage(
                    fileBytes = outputStream.toByteArray(),
                    fileName = fileName ?: error("No file name provided!"),
                    newImage = newImage,
                )

                imagesAccess.recordNewImage(newImage)

                call.respond(HttpStatusCode.Created)
            }
        }
    }
}