package guru.furu.kgaBackend.adapter.db

import guru.furu.kgaBackend.adapter.model.NodeSerializer
import guru.furu.kgaBackend.adapter.model.RelationshipType
import guru.furu.kgaBackend.domain.nodes.Account
import guru.furu.kgaBackend.domain.nodes.Comment
import guru.furu.kgaBackend.domain.nodes.Image
import guru.furu.kgaBackend.domain.nodes.Node
import guru.furu.kgaBackend.domain.nodes.Tag
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
        val query = serializer.serializeUpsertNode(node).query

        database.session().use {
            it.run(query)
        }
    }

    override suspend fun saveNodesBulk(nodes: List<Node>) {
        if (nodes.isEmpty()) {
            return
        }

        val bulkCreateQuery =
            buildString {
                nodes.forEach {
                    appendLine(serializer.serializeUpsertNode(it))
                }
            }

        database.session().use {
            it.run(bulkCreateQuery)
        }
    }

    override suspend fun createImageWithTags(
        image: Image,
        tags: List<Tag>,
    ) {
        val query = serializer.serializeWithRelationship(image, tags, RelationshipType.HAS_TAG)

        database.session().use {
            it.run(query)
        }
    }

    override suspend fun loadImageById(nodeId: UUID): Image {
        val className = Image::class.simpleName ?: error("Could not get class name!")
        val query = serializer.serializeMatchQuery(className, "nodeId: '$nodeId'")

        return loadImageByQueryString(query)
    }

    override suspend fun loadTagById(nodeId: UUID): Tag {
        val query = serializer.getNodeQueryById(nodeId)
        return loadTags(query).first()
    }

    override suspend fun loadCommentById(nodeId: UUID): Comment {
        val query = serializer.getNodeQueryById(nodeId)
        return loadComments(query).first()
    }

    // used for create/update of new nodes
    override suspend fun createWithRelationship(
        from: Node,
        to: Node,
        type: RelationshipType,
    ) {
        val query = serializer.serializeWithRelationship(from, listOf(to), type)

        database.session().use {
            it.run(query)
        }
    }

    override suspend fun loadAccountById(nodeId: UUID): Account {
        val className = Account::class.simpleName ?: error("Could not get class name!")
        val query = serializer.serializeMatchQuery(className, "nodeId: '$nodeId'")

        return loadAccountByQueryString(query)
    }

    override suspend fun loadAccountByEmail(email: String): Account {
        val className = Account::class.simpleName ?: error("Could not get class name!")
        val query = serializer.serializeMatchQuery(className, "email: '$email'")

        return loadAccountByQueryString(query)
    }

    override suspend fun loadCommentsForImage(imageId: UUID): List<Comment> {
        val query = serializer.serializeGetConnectedComponents(imageId, RelationshipType.HAS_COMMENT)
        return loadComments(query)
    }

    override suspend fun loadTagsForImage(imageId: UUID): List<Tag> {
        val query = serializer.serializeGetConnectedComponents(imageId, RelationshipType.HAS_TAG)
        return loadTags(query)
    }

    private fun loadTags(query: String): List<Tag> {
        val tagList: MutableList<Tag> = mutableListOf()

        database.session().use {
            val result = it.run(query)

            while (result.hasNext()) {
                val rec = result.next()
                val fields = rec.fields()

                fields.forEach { field ->
                    val node = field.value().asNode()
                    tagList.add(
                        Tag(
                            nodeId = UUID.fromString(node["nodeId"].asString()),
                            title = node["title"].asString(),
                        ),
                    )
                }
            }
        }

        return tagList
    }

    private fun loadComments(query: String): List<Comment> {
        val commentList: MutableList<Comment> = mutableListOf()

        database.session().use {
            val result = it.run(query)

            while (result.hasNext()) {
                val rec = result.next()
                val fields = rec.fields()

                fields.forEach { field ->
                    val node = field.value().asNode()
                    commentList.add(
                        Comment(
                            nodeId = UUID.fromString(node["nodeId"].asString()),
                            authorId = UUID.fromString(node["authorId"].asString()),
                            onImageId = UUID.fromString(node["onImageId"].asString()),
                            text = node["text"].asString(),
                            date = Instant.fromEpochSeconds(node["date"].asLong()),
                        ),
                    )
                }
            }
        }

        return commentList
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

                // for some reason the filename comes out of the graph wrapped in extra escaped quotes sometimes
                val fileName = fields["fileName"].toString().removePrefix("\"").removeSuffix("\"")

                foundImage =
                    Image(
                        nodeId = UUID.fromString(fields["nodeId"].asString()),
                        title = fields["title"].asString(),
                        description = fields["description"].asString(),
                        uploaderId = UUID.fromString(fields["uploaderId"].asString()),
                        uploadedAt = Instant.fromEpochSeconds(fields["uploadedAt"].asLong()),
                        fileName = fileName,
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
                        nodeId = UUID.fromString(fields["nodeId"].asString()),
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
