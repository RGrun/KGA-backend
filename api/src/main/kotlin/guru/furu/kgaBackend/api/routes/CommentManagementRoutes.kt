package guru.furu.kgaBackend.api.routes

import guru.furu.kgaBackend.adapter.nodeaccess.CommentAccess
import guru.furu.kgaBackend.adapter.toDomain
import guru.furu.kgaBackend.client.dto.NewCommentDTO
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.commentManagementRoutes(commentAccess: CommentAccess) {
    routing {
        route("/comments") {
//            get("/by-email/{email}") {
//                val email = call.parameters["email"] ?: error("No email provided")
//
//                accountAccess.loadAccountByEmail(email = email)?.let {
//                    call.respond(it.toAccountDTO())
//                } ?: suspend {
//                    call.respond(HttpStatusCode.NotFound)
//                }
//            }

            post("/new") {
                val comment = call.receive<NewCommentDTO>()
                commentAccess.addNewComment(comment.toDomain())

                call.respond(HttpStatusCode.Accepted)
            }
        }
    }
}
