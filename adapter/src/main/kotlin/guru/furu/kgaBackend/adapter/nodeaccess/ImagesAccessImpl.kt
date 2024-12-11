package guru.furu.kgaBackend.adapter.nodeaccess

import guru.furu.kgaBackend.adapter.db.DatabaseAccess
import guru.furu.kgaBackend.adapter.model.RelationshipType
import guru.furu.kgaBackend.domain.nodes.Image
import guru.furu.kgaBackend.domain.nodes.incoming.NewImage
import kotlinx.datetime.Clock
import java.util.UUID

class ImagesAccessImpl(
    private val databaseAccess: DatabaseAccess,
) : ImagesAccess {
    override suspend fun recordNewImage(newImage: NewImage) {
        val uploaderAccount = databaseAccess.loadAccountById(newImage.uploaderAccountId)

        val imageRecord =
            Image(
                id = UUID.randomUUID(),
                title = newImage.title,
                description = newImage.description,
                uploadedAt = Clock.System.now(),
                uploaderId = uploaderAccount.id,
            )

        databaseAccess.createWithRelationship(uploaderAccount, imageRecord, RelationshipType.UPLOADED)
    }

    override suspend fun fetchImageDetails(imageId: UUID): Image = databaseAccess.loadImageById(imageId)
}
