package guru.furu.kgaBackend.adapter.nodeaccess

import guru.furu.kgaBackend.adapter.db.DatabaseAccess
import guru.furu.kgaBackend.adapter.model.RelationshipType
import guru.furu.kgaBackend.client.dto.incoming.NewTagDTO
import guru.furu.kgaBackend.domain.nodes.Image
import guru.furu.kgaBackend.domain.nodes.Tag
import guru.furu.kgaBackend.domain.nodes.incoming.NewImage
import kotlinx.datetime.Clock
import java.util.UUID

class ImagesAccessImpl(
    private val databaseAccess: DatabaseAccess,
) : ImagesAccess {
    override suspend fun recordNewImage(
        fileName: String,
        newImage: NewImage,
        tags: List<NewTagDTO>?,
    ) {
        val uploaderAccount =
            databaseAccess.loadAccountById(newImage.uploaderAccountId)
                ?: error("Could not find account with id: ${newImage.uploaderAccountId}!")

        val imageRecord =
            Image(
                nodeId = UUID.randomUUID(),
                title = newImage.title,
                description = newImage.description,
                uploadedAt = Clock.System.now(),
                uploaderId = uploaderAccount.nodeId,
                fileName = fileName,
            )

        databaseAccess.createWithRelationship(uploaderAccount, imageRecord, RelationshipType.UPLOADED)

        // this can be a combination of old and new tags.
        // new tags have null ids. New IDs for them are generated here.
        val tagNodes =
            tags?.map { tag ->
                Tag(
                    nodeId = tag.nodeId?.let { UUID.fromString(it) } ?: UUID.randomUUID(),
                    title = tag.title,
                )
            }

        databaseAccess.createImageWithTags(imageRecord, tagNodes ?: emptyList())
    }

    override suspend fun fetchImageDetails(imageId: UUID): Image? = databaseAccess.loadImageById(imageId)
}
