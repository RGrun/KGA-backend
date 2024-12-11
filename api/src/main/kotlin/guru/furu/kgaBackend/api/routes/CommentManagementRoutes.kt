package guru.furu.kgaBackend.api.routes

import guru.furu.kgaBackend.adapter.nodeaccess.CommentAccess
import guru.furu.kgaBackend.adapter.toDTO
import guru.furu.kgaBackend.adapter.toDomain
import guru.furu.kgaBackend.client.dto.incoming.NewCommentDTO
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.util.UUID

fun Application.commentManagementRoutes(commentAccess: CommentAccess) {
    routing {
        route("/comments") {
            get("/by-image-id/{imageId}") {
                val imageId = call.parameters["imageId"] ?: error("No imageId provided")

                val comments =
                    commentAccess.getCommentsForImage(
                        UUID.fromString(imageId),
                    ).map { it.toDTO() }

                call.respond(comments)
            }

            post("/new") {
                val comment = call.receive<NewCommentDTO>()
                commentAccess.addNewComment(comment.toDomain())

                call.respond(HttpStatusCode.Accepted)
            }
        }
    }
}
