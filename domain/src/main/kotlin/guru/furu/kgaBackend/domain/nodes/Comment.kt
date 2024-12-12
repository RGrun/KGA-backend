package guru.furu.kgaBackend.domain.nodes

import kotlinx.datetime.Instant
import java.util.UUID

data class Comment(
    override val nodeId: UUID,
    val text: String,
    val date: Instant,
    // TODO ideally I wouldn't need these two here. Once I have a better loading system
    // I will just load these from the incoming node edges. I think this might be faster for the
    // current system though.
    val authorId: UUID,
    val onImageId: UUID,
) : Node
