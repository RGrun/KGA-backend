package guru.furu.kgaBackend.adapter.model

import guru.furu.kgaBackend.domain.nodes.Node
import org.neo4j.driver.Record

class NodeDeserializerImpl : NodeDeserializer {
    override suspend fun deserializeNodes(records: List<Record>): List<Node> {
        TODO()

//        return records.map { nodeValues ->
//            //return nodeValues.
//        }
    }
}
