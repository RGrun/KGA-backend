package guru.furu.kgaBackend.adapter.nodeaccess

import guru.furu.kgaBackend.domain.nodes.incoming.NewComment

interface CommentAccess {
    suspend fun addNewComment(newComment: NewComment)
}
