package guru.furu.kgaBackend.api.routes

import guru.furu.kgaBackend.adapter.nodeaccess.AccountAccess
import guru.furu.kgaBackend.adapter.toAccountDTO
import guru.furu.kgaBackend.adapter.toDomain
import guru.furu.kgaBackend.client.dto.incoming.NewAccountDTO
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun Application.accountManagementRoutes(accountAccess: AccountAccess) {
    routing {
        route("/account") {
            get("/by-email/{email}") {
                val email = call.parameters["email"] ?: error("No email provided")

                accountAccess.loadAccountByEmail(email = email)?.let {
                    call.respond(it.toAccountDTO())
                } ?: suspend {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            post("/new") {
                val account = call.receive<NewAccountDTO>()
                accountAccess.saveAccount(account.toDomain())

                call.respond(HttpStatusCode.Accepted)
            }
        }
    }
}
