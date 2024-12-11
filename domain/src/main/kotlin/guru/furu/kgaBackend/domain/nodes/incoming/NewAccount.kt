package guru.furu.kgaBackend.domain.nodes.incoming

data class NewAccount(
    val userName: String,
    val email: String,
    val password: String,
)
