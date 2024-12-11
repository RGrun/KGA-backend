package guru.furu.kgaBackend.adapter.nodeaccess

import guru.furu.kgaBackend.domain.nodes.Account
import guru.furu.kgaBackend.domain.nodes.incoming.NewAccount

interface AccountAccess {
    suspend fun saveAccount(newAccount: NewAccount)

    suspend fun loadAccountByEmail(email: String): Account?
}
