package guru.furu.kgaBackend.adapter.nodeaccess

import guru.furu.kgaBackend.client.dto.incoming.NewTagDTO
import guru.furu.kgaBackend.domain.nodes.Tag
import java.util.UUID

interface TagsAccess {
    suspend fun tagById(tagId: UUID): Tag

    suspend fun addNewTag(newTag: NewTagDTO)
}
