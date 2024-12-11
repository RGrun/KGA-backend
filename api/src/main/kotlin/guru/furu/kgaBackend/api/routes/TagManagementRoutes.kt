package guru.furu.kgaBackend.api.routes

import guru.furu.kgaBackend.adapter.nodeaccess.TagsAccess
import guru.furu.kgaBackend.adapter.toDTO
import guru.furu.kgaBackend.client.dto.incoming.NewTagDTO
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.util.UUID

fun Application.tagManagementRoutes(tagsAccess: TagsAccess) {
    routing {
        route("/tags") {
            get("/by-tag-id/{tagId}") {
                val tagId = call.parameters["tagId"] ?: error("No tagId provided")
                val tag = tagsAccess.tagById(UUID.fromString(tagId))
                call.respond(tag.toDTO())
            }

            post("/new") {
                val tag = call.receive<NewTagDTO>()
                tagsAccess.addNewTag(tag)
                call.respond(HttpStatusCode.Accepted)
            }
        }
    }
}
