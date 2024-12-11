package guru.furu.kgaBackend.adapter.nodeaccess

import guru.furu.kgaBackend.domain.nodes.Comment
import guru.furu.kgaBackend.domain.nodes.incoming.NewComment
import java.util.UUID

interface CommentAccess {
    suspend fun addNewComment(newComment: NewComment)

    suspend fun getCommentsForImage(imageId: UUID): List<Comment>
}
