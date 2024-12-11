package guru.furu.kgaBackend.adapter.model

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

    override suspend fun serializeCreateNode(node: Node): QueryNamePair =
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
        to: Node,
        relationshipType: RelationshipType,
    ): String {
        val fromSer = serializeCreateNode(from)
        val toSer = serializeCreateNode(to)

        return buildString {
            appendLine(fromSer.query)
            appendLine(toSer.query)
            appendLine("MERGE (${fromSer.name})-[${relationshipType.serValue}]->(${toSer.name})")
        }
    }

    override suspend fun serializeMatchQuery(
        nodeName: String,
        matchClause: String,
    ): String {
        return "MATCH (n:$nodeName {%%%}) return n".replace("%%%", matchClause)
    }

    override suspend fun getNodeQueryById(id: UUID): String {
        return "MATCH (n {id: '$id'}) return n"
    }

    override suspend fun serializeGetConnectedComponents(
        id: UUID,
        relationshipType: RelationshipType,
        reverse: Boolean,
    ): String {
        return if (reverse) {
            "MATCH (n {id: '$id'})<-[${relationshipType.serValue}]-(connected) return n"
        } else {
            "MATCH (n {id: '$id'})-[${relationshipType.serValue}]->(connected) return connected"
        }
    }

    private fun serializeAccount(acct: Account): QueryNamePair {
        val propsStr =
            buildString {
                append("id: '${acct.id}', ")
                append("userName: '${acct.userName}', ")
                append("email: '${acct.email}', ")
                append("createdAt: ${acct.createdAt.epochSeconds}, ")
                append("lastLogin: ${acct.lastLogin.epochSeconds}")
            }

        return QueryNamePair("MERGE (account:Account {%%%})".replace("%%%", propsStr), "account")
    }

    private fun serializeAuth(auth: Auth): QueryNamePair {
        val propsStr =
            buildString {
                append("id: '${auth.id}', ")
                append("password: '${auth.password}'")
            }

        return QueryNamePair("MERGE (auth:Auth {%%%})".replace("%%%", propsStr), "auth")
    }

    private fun serializeComment(comment: Comment): QueryNamePair {
        val propsStr =
            buildString {
                append("id: '${comment.id}', ")
                append("authorId: '${comment.authorId}', ")
                append("onImageId: '${comment.onImageId}', ")
                append("text: '${comment.text}', ")
                append("date: ${comment.date.epochSeconds}")
            }

        return QueryNamePair("MERGE (comment:Comment {%%%})".replace("%%%", propsStr), "comment")
    }

    private fun serializeImage(image: Image): QueryNamePair {
        val propsStr =
            buildString {
                append("id: '${image.id}', ")
                append("description: '${image.description}', ")
                append("title: '${image.title}', ")
                append("uploaderId: '${image.uploaderId}', ")
                append("uploadedAt: ${image.uploadedAt.epochSeconds} ")
            }

        return QueryNamePair("MERGE (img:Image {%%%})".replace("%%%", propsStr), "img")
    }

    private fun serializeTag(tag: Tag): QueryNamePair {
        val propsStr =
            buildString {
                append("id: '${tag.id}', ")
                append("title: '${tag.title}'")
            }

        return QueryNamePair("MERGE (tag:Tag {%%%})".replace("%%%", propsStr), "tag")
    }
}
