package guru.furu.kgaBackend.api

import guru.furu.kgaBackend.adapter.db.Neo4JDatabaseAccessImpl
import guru.furu.kgaBackend.adapter.fs.LocalFilesystemImagesFilesystemAccessImpl
import guru.furu.kgaBackend.adapter.model.Neo4jNodeSerializerImpl
import guru.furu.kgaBackend.adapter.nodeaccess.AccountAccessImpl
import guru.furu.kgaBackend.adapter.nodeaccess.CommentAccessImpl
import guru.furu.kgaBackend.adapter.nodeaccess.ImagesAccessImpl
import guru.furu.kgaBackend.api.routes.accountManagementRoutes
import guru.furu.kgaBackend.api.routes.commentManagementRoutes
import guru.furu.kgaBackend.api.routes.imageManagementRoutes
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val graphDatabaseAccess =
        Neo4JDatabaseAccessImpl(
            uri = "bolt://localhost:7687",
            username = "testuser",
            password = "testuser",
            serializer = Neo4jNodeSerializerImpl(),
        )

    val accountManager = AccountAccessImpl(databaseAccess = graphDatabaseAccess)

    val imagesAccess = ImagesAccessImpl(graphDatabaseAccess)
    val filesystemAccess = LocalFilesystemImagesFilesystemAccessImpl("R:/kga/images")

    val commentAccess = CommentAccessImpl(databaseAccess = graphDatabaseAccess)

    configureSerialization()
    configureSecurity()
    configureRouting()
    accountManagementRoutes(accountManager)
    imageManagementRoutes(imagesAccess, filesystemAccess)
    commentManagementRoutes(commentAccess)
}
