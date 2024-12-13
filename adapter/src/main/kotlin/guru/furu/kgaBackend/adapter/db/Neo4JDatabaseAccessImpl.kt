package guru.furu.kgaBackend.adapter.db

import guru.furu.kgaBackend.adapter.model.CypherQueryBuilder
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
        val query =
            CypherQueryBuilder()
                .createOrUpdateNode(node)
                .build()

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
                    val query =
                        CypherQueryBuilder()
                            .createOrUpdateNode(it)
                            .build()
                    appendLine(query)
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
        val query =
            CypherQueryBuilder()
                .createWithRelationship(image, tags, RelationshipType.HAS_TAG)
                .build()

        database.session().use {
            it.run(query)
        }
    }

    override suspend fun loadImageById(nodeId: UUID): Image? {
        val className = Image::class.simpleName ?: error("Could not get class name!")
        val query =
            CypherQueryBuilder()
                .matchOn(className, "nodeId: '$nodeId'")
                .build()

        return loadImagesByQueryString(query).first()
    }

    override suspend fun loadTagById(nodeId: UUID): Tag {
        val query =
            CypherQueryBuilder()
                .getNodeQueryById(nodeId)
                .build()

        return loadTags(query).first()
    }

    override suspend fun loadCommentById(nodeId: UUID): Comment {
        val query =
            CypherQueryBuilder()
                .getNodeQueryById(nodeId)
                .build()

        return loadComments(query).first()
    }

    // used for create/update of new nodes
    override suspend fun createWithRelationship(
        from: Node,
        to: Node,
        type: RelationshipType,
    ) {
        val query =
            CypherQueryBuilder()
                .createWithRelationship(from, listOf(to), type)
                .build()

        database.session().use {
            it.run(query)
        }
    }

    override suspend fun loadAccountById(nodeId: UUID): Account? {
        val className = Account::class.simpleName ?: error("Could not get class name!")
        val query =
            CypherQueryBuilder()
                .matchOn(className, "nodeId: '$nodeId'")
                .build()

        return loadAccountByQueryString(query)
    }

    override suspend fun loadAccountByEmail(email: String): Account? {
        val className = Account::class.simpleName ?: error("Could not get class name!")
        val query =
            CypherQueryBuilder()
                .matchOn(className, "email: '$email'")
                .build()

        return loadAccountByQueryString(query)
    }

    override suspend fun loadCommentsForImage(imageId: UUID): List<Comment> {
        val query =
            CypherQueryBuilder()
                .serializeGetConnectedComponents(imageId, RelationshipType.HAS_COMMENT)
                .build()

        return loadComments(query)
    }

    override suspend fun loadTagsForImage(imageId: UUID): List<Tag> {
        val query =
            CypherQueryBuilder()
                .serializeGetConnectedComponents(imageId, RelationshipType.HAS_TAG)
                .build()

        return loadTags(query)
    }

    override suspend fun loadImagesForAccount(accountId: UUID): List<Image> {
        val query =
            CypherQueryBuilder()
                .serializeGetConnectedComponents(accountId, RelationshipType.UPLOADED)
                .build()

        return loadImagesByQueryString(query)
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
    private fun loadImagesByQueryString(query: String): List<Image> {
        val imageList: MutableList<Image> = mutableListOf()

        database.session().use {
            val result = it.run(query)
            while (result.hasNext()) {
                val rec = result.next()
                val fields = rec.fields()

                fields.forEach { field ->
                    val node = field.value().asNode()
                    // for some reason the filename comes out of the graph wrapped in extra escaped quotes sometimes
                    val fileName = node["fileName"].toString().removePrefix("\"").removeSuffix("\"")
                    imageList.add(
                        Image(
                            nodeId = UUID.fromString(node["nodeId"].asString()),
                            title = node["title"].asString(),
                            description = node["description"].asString(),
                            uploaderId = UUID.fromString(node["uploaderId"].asString()),
                            uploadedAt = Instant.fromEpochSeconds(node["uploadedAt"].asLong()),
                            fileName = fileName,
                        ),
                    )
                }
            }
        }

        return imageList
    }

    private fun loadAccountByQueryString(query: String): Account? {
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

        return foundAccount
    }
}
