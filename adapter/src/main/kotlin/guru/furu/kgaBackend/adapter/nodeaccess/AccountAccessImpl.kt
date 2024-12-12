package guru.furu.kgaBackend.adapter.nodeaccess

import guru.furu.kgaBackend.adapter.db.DatabaseAccess
import guru.furu.kgaBackend.adapter.model.RelationshipType
import guru.furu.kgaBackend.domain.nodes.Account
import guru.furu.kgaBackend.domain.nodes.Auth
import guru.furu.kgaBackend.domain.nodes.incoming.NewAccount
import kotlinx.datetime.Clock
import java.util.UUID

class AccountAccessImpl(
    private val databaseAccess: DatabaseAccess,
) : AccountAccess {
    override suspend fun saveAccount(newAccount: NewAccount) {
        val account =
            Account(
                nodeId = UUID.randomUUID(),
                userName = newAccount.userName,
                email = newAccount.email,
                createdAt = Clock.System.now(),
                lastLogin = Clock.System.now(),
            )

        val auth =
            Auth(
                nodeId = UUID.randomUUID(),
                password = newAccount.password,
            )

        databaseAccess.createWithRelationship(account, auth, RelationshipType.HAS_AUTH)
    }

    override suspend fun loadAccountByEmail(email: String): Account? = databaseAccess.loadAccountByEmail(email)
}
