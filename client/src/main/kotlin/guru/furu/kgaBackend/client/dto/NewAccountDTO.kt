package guru.furu.kgaBackend.client.dto

import kotlinx.serialization.Serializable

@Serializable
data class NewAccountDTO(
    val userName: String,
    val email: String,
    val password: String,
)
