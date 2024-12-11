package guru.furu.kgaBackend.adapter.model

import guru.furu.kgaBackend.domain.nodes.Node
import org.neo4j.driver.Record

interface NodeDeserializer {
    suspend fun deserializeNodes(records: List<Record>): List<Node>
}
