package guru.furu.kgaBackend.adapter.db

import guru.furu.kgaBackend.adapter.model.NodeSerializer
import guru.furu.kgaBackend.adapter.model.RelationshipType
import guru.furu.kgaBackend.domain.nodes.Account
import guru.furu.kgaBackend.domain.nodes.Image
import guru.furu.kgaBackend.domain.nodes.Node
import kotlinx.datetime.Instant
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.slf4j.LoggerFactory
import java.util.UUID

class Neo4JDatabaseAccessImpl(
    private val uri: String,
    private val username: String,
    private val password: String,
    private val serializer: NodeSerializer,
) : DatabaseAccess {
    private val logger = LoggerFactory.getLogger(Neo4JDatabaseAccessImpl::class.java)
    private val database: Driver =
        GraphDatabase.driver(
            // uri =
            uri,
            // authToken =
            AuthTokens.basic(username, password),
        )

    override suspend fun saveNode(node: Node) {
        val query = serializer.serializeCreateNode(node).query

        database.session().use {
            it.run(query)
        }
    }

    override suspend fun loadImageById(id: UUID): Image {
        val className = Image::class.simpleName ?: error("Could not get class name!")
        val query = serializer.serializeMatchQuery(className, "id: '$id'")

        return loadImageByQueryString(query)
    }

    // used for create/update of new nodes
    override suspend fun createWithRelationship(
        from: Node,
        to: Node,
        type: RelationshipType,
    ) {
        val query = serializer.serializeWithRelationship(from, to, type)

        database.session().use {
            it.run(query)
        }
    }

    override suspend fun loadAccountById(id: UUID): Account {
        val className = Account::class.simpleName ?: error("Could not get class name!")
        val query = serializer.serializeMatchQuery(className, "id: '$id'")

        return loadAccountByQueryString(query)
    }

    override suspend fun loadAccountByEmail(email: String): Account {
        val className = Account::class.simpleName ?: error("Could not get class name!")
        val query = serializer.serializeMatchQuery(className, "email: '$email'")

        return loadAccountByQueryString(query)
    }

    // TODO: do these better in a more generic way so I don't need one func per Node type
    private fun loadImageByQueryString(query: String): Image {
        var foundImage: Image? = null

        database.session().use {
            val result = it.run(query)
            while (result.hasNext()) {
                val rec = result.next()

                // should only be one result
                val fields = rec.fields()[0].value().asNode()

                foundImage =
                    Image(
                        id = UUID.fromString(fields["id"].asString()),
                        title = fields["title"].asString(),
                        description = fields["description"].asString(),
                        uploaderId = UUID.fromString(fields["uploaderId"].asString()),
                        uploadedAt = Instant.fromEpochSeconds(fields["uploadedAt"].asLong()),
                    )
            }
        }

        return foundImage ?: error("Could not load image! query: $query")
    }

    private fun loadAccountByQueryString(query: String): Account {
        var foundAccount: Account? = null

        database.session().use {
            val result = it.run(query)
            while (result.hasNext()) {
                val rec = result.next()

                // should only be one result
                val fields = rec.fields()[0].value().asNode()

                foundAccount =
                    Account(
                        id = UUID.fromString(fields["id"].asString()),
                        userName = fields["userName"].asString(),
                        email = fields["email"].asString(),
                        createdAt = Instant.fromEpochSeconds(fields["createdAt"].asLong()),
                        lastLogin = Instant.fromEpochSeconds(fields["lastLogin"].asLong()),
                    )
            }
        }

        return foundAccount ?: error("Could not load account! query: $query")
    }
}