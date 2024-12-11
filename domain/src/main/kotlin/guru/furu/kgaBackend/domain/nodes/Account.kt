package guru.furu.kgaBackend.domain.nodes

import kotlinx.datetime.Instant
import java.util.UUID

data class Account(
    override val id: UUID,
    val userName: String,
    val email: String,
    val createdAt: Instant,
    val lastLogin: Instant,
) : Node
