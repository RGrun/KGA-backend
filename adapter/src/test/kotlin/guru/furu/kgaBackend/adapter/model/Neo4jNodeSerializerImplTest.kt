package guru.furu.kgaBackend.adapter.model

import guru.furu.kgaBackend.adapter.toDomain
import guru.furu.kgaBackend.client.dto.incoming.NewAccountDTO
import guru.furu.kgaBackend.domain.nodes.Account
import guru.furu.kgaBackend.domain.nodes.Auth
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.UUID
import kotlin.test.Test

class Neo4jNodeSerializerImplTest {
    companion object {
        private val serializer = Neo4jNodeSerializerImpl()

        val newAccount =
            NewAccountDTO(
                userName = "testGuy",
                email = "test@whatever.com",
                password = "lol",
            ).toDomain()

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
                password = "lol",
            )
    }

    @Test
    fun `serialize new account nodes`() =
        runBlocking {
            val res = serializer.serializeUpsertNode(account).query

            assertTrue(res.contains(account.userName))
            assertTrue(res.contains(account.email))
        }

    @Test
    fun `serialize new auth nodes`() =
        runBlocking {
            val res = serializer.serializeUpsertNode(auth).query

            assertTrue(res.contains(auth.nodeId.toString()))
            assertTrue(res.contains(auth.password))
        }

    @Test
    fun `serialize creating relationships`() =
        runBlocking {
            RelationshipType.entries.forEach {
                val res = serializer.serializeWithRelationship(account, listOf(auth), it)

                assertTrue(res.contains(account.nodeId.toString()))
                assertTrue(res.contains(auth.nodeId.toString()))
                assertTrue(res.contains(it.serValue))
            }
        }
}
