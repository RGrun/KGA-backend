package guru.furu.kgaBackend.client.dto.incoming

import kotlinx.serialization.Serializable

@Serializable
data class NewAccountDTO(
    val userName: String,
    val email: String,
    val password: String,
)
