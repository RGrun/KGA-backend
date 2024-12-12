package guru.furu.kgaBackend.adapter.model

import guru.furu.kgaBackend.adapter.util.StringUtils
import guru.furu.kgaBackend.domain.nodes.Account
import guru.furu.kgaBackend.domain.nodes.Auth
import guru.furu.kgaBackend.domain.nodes.Comment
import guru.furu.kgaBackend.domain.nodes.Image
import guru.furu.kgaBackend.domain.nodes.Node
import guru.furu.kgaBackend.domain.nodes.Tag
import java.util.UUID

class Neo4jNodeSerializerImpl : NodeSerializer {
    data class QueryNamePair(
        val query: String,
        val name: String,
    )

    override suspend fun serializeUpsertNode(node: Node): QueryNamePair =
        when (node) {
            is Account -> serializeAccount(node)
            is Auth -> serializeAuth(node)
            is Comment -> serializeComment(node)
            is Image -> serializeImage(node)
            is Tag -> serializeTag(node)
        }

    // used for create and update
    // TODO replace with some kind of builder pattern to build cypher queries
    override suspend fun serializeWithRelationship(
        from: Node,
        to: List<Node>,
        relationshipType: RelationshipType,
    ): String {
        val fromSer = serializeUpsertNode(from)

        val toCreateCalls =
            to.map {
                serializeUpsertNode(it)
            }

        val relationshipCalls =
            toCreateCalls.map {
                "MERGE (${fromSer.name})-[${relationshipType.serValue}]->(${it.name})"
            }

        return buildString {
            appendLine(fromSer.query)

            toCreateCalls.forEach {
                appendLine(it.query)
            }

            relationshipCalls.forEach {
                appendLine(it)
            }
        }
    }

    override suspend fun serializeMatchQuery(
        nodeName: String,
        matchClause: String,
    ): String {
        return "MATCH (n:$nodeName {%%%}) return n".replace("%%%", matchClause)
    }

    override suspend fun getNodeQueryById(nodeId: UUID): String {
        return "MATCH (n {nodeId: '$nodeId'}) return n"
    }

    override suspend fun serializeGetConnectedComponents(
        nodeId: UUID,
        relationshipType: RelationshipType,
        reverse: Boolean,
    ): String {
        return if (reverse) {
            "MATCH (n {nodeId: '$nodeId'})<-[${relationshipType.serValue}]-(connected) return n"
        } else {
            "MATCH (n {nodeId: '$nodeId'})-[${relationshipType.serValue}]->(connected) return connected"
        }
    }

    private fun serializeAccount(acct: Account): QueryNamePair {
        val propsStr =
            buildString {
                append("nodeId: '${acct.nodeId}', ")
                append("userName: '${acct.userName}', ")
                append("email: '${acct.email}', ")
                append("createdAt: ${acct.createdAt.epochSeconds}, ")
                append("lastLogin: ${acct.lastLogin.epochSeconds}")
            }

        val randomName = StringUtils.randomString()

        return QueryNamePair("MERGE ($randomName:Account {%%%})".replace("%%%", propsStr), randomName)
    }

    private fun serializeAuth(auth: Auth): QueryNamePair {
        val propsStr =
            buildString {
                append("nodeId: '${auth.nodeId}', ")
                append("password: '${auth.password}'")
            }

        val randomName = StringUtils.randomString()

        return QueryNamePair("MERGE ($randomName:Auth {%%%})".replace("%%%", propsStr), randomName)
    }

    private fun serializeComment(comment: Comment): QueryNamePair {
        val propsStr =
            buildString {
                append("nodeId: '${comment.nodeId}', ")
                append("authorId: '${comment.authorId}', ")
                append("onImageId: '${comment.onImageId}', ")
                append("text: '${comment.text}', ")
                append("date: ${comment.date.epochSeconds}")
            }

        val randomName = StringUtils.randomString()

        return QueryNamePair("MERGE ($randomName:Comment {%%%})".replace("%%%", propsStr), randomName)
    }

    private fun serializeImage(image: Image): QueryNamePair {
        val fileName = image.fileName

        val propsStr =
            buildString {
                append("nodeId: '${image.nodeId}', ")
                append("description: '${image.description}', ")
                append("title: '${image.title}', ")
                append("uploaderId: '${image.uploaderId}', ")
                append("uploadedAt: ${image.uploadedAt.epochSeconds}, ")
                append("fileName: '$fileName' ")
            }

        val randomName = StringUtils.randomString()

        return QueryNamePair("MERGE ($randomName:Image {%%%})".replace("%%%", propsStr), randomName)
    }

    private fun serializeTag(tag: Tag): QueryNamePair {
        val propsStr =
            buildString {
                append("nodeId: '${tag.nodeId}', ")
                append("title: '${tag.title}'")
            }

        val randomName = StringUtils.randomString()

        return QueryNamePair("MERGE ($randomName:Tag {%%%})".replace("%%%", propsStr), randomName)
    }
}
