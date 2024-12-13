package guru.furu.kgaBackend.adapter.model

import guru.furu.kgaBackend.adapter.util.StringUtils
import guru.furu.kgaBackend.domain.nodes.Account
import guru.furu.kgaBackend.domain.nodes.Comment
import guru.furu.kgaBackend.domain.nodes.Image
import guru.furu.kgaBackend.domain.nodes.Tag
import kotlinx.datetime.Clock
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertContains

class CypherQueryBuilderTest {
    companion object {
        private val accountNode =
            Account(
                nodeId = UUID.randomUUID(),
                userName = "randoman",
                email = "randoman@what.com",
                createdAt = Clock.System.now(),
                lastLogin = Clock.System.now(),
            )

        private val imageNode =
            Image(
                nodeId = UUID.randomUUID(),
                uploadedAt = Clock.System.now(),
                uploaderId = accountNode.nodeId,
                title = "cool image",
                description = "wow this is a cool image",
                fileName = "coolimage.jpg",
            )

        private val commentNode =
            Comment(
                nodeId = UUID.randomUUID(),
                text = "this is a cool comment",
                date = Clock.System.now(),
                authorId = accountNode.nodeId,
                onImageId = imageNode.nodeId,
            )

        private val tagNodes =
            listOf(
                Tag(
                    nodeId = UUID.randomUUID(),
                    title = StringUtils.randomString(),
                ),
                Tag(
                    nodeId = UUID.randomUUID(),
                    title = StringUtils.randomString(),
                ),
                Tag(
                    nodeId = UUID.randomUUID(),
                    title = StringUtils.randomString(),
                ),
                Tag(
                    nodeId = UUID.randomUUID(),
                    title = StringUtils.randomString(),
                ),
                Tag(
                    nodeId = UUID.randomUUID(),
                    title = StringUtils.randomString(),
                ),
            )
    }

    @Test
    fun `test create or update query`() {
        val query =
            CypherQueryBuilder()
                .createOrUpdateNode(accountNode)
                .createOrUpdateNode(imageNode)
                .build()

        assertContains(query, accountNode.nodeId.toString())
        assertContains(query, imageNode.nodeId.toString())
    }

    @Test
    fun `create image with relationship query`() {
        val query =
            CypherQueryBuilder()
                .createWithRelationship(accountNode, listOf(imageNode), RelationshipType.UPLOADED)
                .build()

        assertContains(query, accountNode.nodeId.toString())
        assertContains(query, imageNode.nodeId.toString())
        assertContains(query, RelationshipType.UPLOADED.serValue)
    }

    @Test
    fun `create image with tags query`() {
        val query =
            CypherQueryBuilder()
                .createWithRelationship(imageNode, tagNodes, RelationshipType.HAS_TAG)
                .build()

        assertContains(query, imageNode.nodeId.toString())
        assertContains(query, RelationshipType.HAS_TAG.serValue)
    }
}
