package guru.furu.kgaBackend.client.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class AccountDTO(
    val id: String,
    val userName: String,
    val email: String,
    val lastLogin: Instant,
    // TODO: add images here or load separately?
)
