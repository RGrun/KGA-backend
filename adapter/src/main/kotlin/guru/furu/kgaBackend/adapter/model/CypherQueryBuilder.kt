package guru.furu.kgaBackend.adapter.model

import guru.furu.kgaBackend.adapter.util.StringUtils
import guru.furu.kgaBackend.domain.nodes.Account
import guru.furu.kgaBackend.domain.nodes.Auth
import guru.furu.kgaBackend.domain.nodes.Comment
import guru.furu.kgaBackend.domain.nodes.Image
import guru.furu.kgaBackend.domain.nodes.Node
import guru.furu.kgaBackend.domain.nodes.Tag
import java.util.UUID

class CypherQueryBuilder {
    data class QueryNamePair(
        val query: String,
        val queryTag: String,
    )

    // node hash->queryNamePair
    private val nodeRefs: MutableMap<Int, QueryNamePair> = mutableMapOf()
    private val stringBuilder: StringBuilder = StringBuilder()

    fun createOrUpdateNode(node: Node) =
        apply {
            val queryPair = getQueryPair(node)
            stringBuilder.appendLine(queryPair.query)
        }

    fun createWithRelationship(
        from: Node,
        to: List<Node>,
        relationshipType: RelationshipType,
    ) = apply {
        val fromQueryPair = getQueryPair(from)

        val toQueryPairs =
            to.map {
                getQueryPair(it)
            }

        val relationshipCalls =
            toQueryPairs.map {
                "MERGE (${fromQueryPair.queryTag})-[${relationshipType.serValue}]->(${it.queryTag})"
            }

        stringBuilder.appendLine(fromQueryPair.query)

        toQueryPairs.forEach {
            stringBuilder.appendLine(it.query)
        }

        relationshipCalls.forEach {
            stringBuilder.appendLine(it)
        }
    }

    fun matchOn(
        nodeTypeName: String,
        matchClause: String,
    ) = apply {
        stringBuilder.appendLine("MATCH (n:$nodeTypeName {%%%}) return n".replace("%%%", matchClause))
    }

    fun getNodeQueryById(nodeId: UUID) =
        apply {
            stringBuilder.appendLine("MATCH (n {nodeId: '$nodeId'}) return n")
        }

    fun serializeGetConnectedComponents(
        nodeId: UUID,
        relationshipType: RelationshipType,
        reverse: Boolean = false,
    ) = apply {
        if (reverse) {
            stringBuilder.appendLine("MATCH (n {nodeId: '$nodeId'})<-[${relationshipType.serValue}]-(connected) return n")
        } else {
            stringBuilder.appendLine("MATCH (n {nodeId: '$nodeId'})-[${relationshipType.serValue}]->(connected) return connected")
        }
    }

    fun build(): String = stringBuilder.toString()

    private fun getQueryPair(node: Node): QueryNamePair {
        val hashCode = node.hashCode()
        return nodeRefs[hashCode]
            ?: serializeUpsertNode(node).let {
                nodeRefs[hashCode] = it
                it
            }
    }

    private fun serializeUpsertNode(node: Node): QueryNamePair =
        when (node) {
            is Account -> serializeAccount(node)
            is Auth -> serializeAuth(node)
            is Comment -> serializeComment(node)
            is Image -> serializeImage(node)
            is Tag -> serializeTag(node)
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

        return QueryNamePair(
            "MERGE ($randomName:Account {%%%})".replace(
                "%%%",
                propsStr,
            ),
            randomName,
        )
    }

    private fun serializeAuth(auth: Auth): QueryNamePair {
        val propsStr =
            buildString {
                append("nodeId: '${auth.nodeId}', ")
                append("password: '${auth.password}'")
            }

        val randomName = StringUtils.randomString()

        return QueryNamePair(
            "MERGE ($randomName:Auth {%%%})".replace(
                "%%%",
                propsStr,
            ),
            randomName,
        )
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

        return QueryNamePair(
            "MERGE ($randomName:Comment {%%%})".replace(
                "%%%",
                propsStr,
            ),
            randomName,
        )
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

        return QueryNamePair(
            "MERGE ($randomName:Image {%%%})".replace(
                "%%%",
                propsStr,
            ),
            randomName,
        )
    }

    private fun serializeTag(tag: Tag): QueryNamePair {
        val propsStr =
            buildString {
                append("nodeId: '${tag.nodeId}', ")
                append("title: '${tag.title}'")
            }

        val randomName = StringUtils.randomString()

        return QueryNamePair(
            "MERGE ($randomName:Tag {%%%})".replace(
                "%%%",
                propsStr,
            ),
            randomName,
        )
    }
}
