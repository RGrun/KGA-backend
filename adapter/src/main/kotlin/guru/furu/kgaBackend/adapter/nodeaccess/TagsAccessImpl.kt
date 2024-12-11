package guru.furu.kgaBackend.adapter.nodeaccess

import guru.furu.kgaBackend.adapter.db.DatabaseAccess
import guru.furu.kgaBackend.client.dto.incoming.NewTagDTO
import guru.furu.kgaBackend.domain.nodes.Tag
import java.util.UUID

class TagsAccessImpl(
    private val databaseAccess: DatabaseAccess,
) : TagsAccess {
    override suspend fun tagById(tagId: UUID): Tag {
        return databaseAccess.loadTagById(tagId)
    }

    override suspend fun addNewTag(newTag: NewTagDTO) {
        val tag =
            Tag(
                id = UUID.randomUUID(),
                title = newTag.title,
            )
        databaseAccess.saveNode(tag)
    }
}
