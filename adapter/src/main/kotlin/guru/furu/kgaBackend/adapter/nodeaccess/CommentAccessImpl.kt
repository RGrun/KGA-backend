package guru.furu.kgaBackend.adapter.nodeaccess

import guru.furu.kgaBackend.adapter.db.DatabaseAccess
import guru.furu.kgaBackend.adapter.model.RelationshipType
import guru.furu.kgaBackend.domain.nodes.Comment
import guru.furu.kgaBackend.domain.nodes.incoming.NewComment
import kotlinx.datetime.Clock
import java.util.UUID

class CommentAccessImpl(
    private val databaseAccess: DatabaseAccess,
) : CommentAccess {
    override suspend fun addNewComment(newComment: NewComment) {
        val author = databaseAccess.loadAccountById(newComment.authorId)
        val image = databaseAccess.loadImageById(newComment.onImageId)

        val comment =
            Comment(
                nodeId = UUID.randomUUID(),
                authorId = newComment.authorId,
                onImageId = newComment.onImageId,
                text = newComment.text,
                date = Clock.System.now(),
            )

        databaseAccess.createWithRelationship(author, comment, RelationshipType.COMMENTED)
        databaseAccess.createWithRelationship(image, comment, RelationshipType.HAS_COMMENT)
    }

    override suspend fun getCommentsForImage(imageId: UUID): List<Comment> {
        return databaseAccess.loadCommentsForImage(imageId)
    }
}
