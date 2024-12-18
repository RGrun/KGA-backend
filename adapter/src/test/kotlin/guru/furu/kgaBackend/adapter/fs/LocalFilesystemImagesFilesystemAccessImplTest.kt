package guru.furu.kgaBackend.adapter.fs

import guru.furu.kgaBackend.domain.nodes.Account
import guru.furu.kgaBackend.domain.nodes.incoming.NewImage
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.File
import java.util.UUID
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFails

class LocalFilesystemImagesFilesystemAccessImplTest {
    companion object {
        val gibbonBytes =
            LocalFilesystemImagesFilesystemAccessImplTest::class.java.getResource("/img/gibbon.PNG")?.readBytes()!!

        private const val FS_ROOT = "./kga/images"

        private val fsAccess = LocalFilesystemImagesFilesystemAccessImpl(FS_ROOT)

        private val account =
            Account(
                nodeId = UUID.randomUUID(),
                userName = "mrGibbon",
                email = "bananas@furu.guru",
                createdAt = Clock.System.now(),
                lastLogin = Clock.System.now(),
            )

        private val newImage =
            NewImage(
                uploaderAccountId = account.nodeId,
                title = "who knows?",
                description = "something witty here",
            )

        private const val FILENAME = "gibbon_upload.png"
    }

    @AfterTest
    fun cleanUpFilesystem(): Unit =
        runBlocking {
            File(FS_ROOT).deleteRecursively()
        }

    @Test
    fun `test gibbon upload, no thumb`() =
        runBlocking {
            fsAccess.saveNewImage(gibbonBytes, FILENAME, newImage, true)
            assertTrue(Path("$FS_ROOT/${account.nodeId}/img/$FILENAME").exists())
        }

    @Test
    fun `test gibbon upload, with thumb`() =
        runBlocking {
            fsAccess.saveNewImage(gibbonBytes, FILENAME, newImage)
            assertTrue(Path("$FS_ROOT/${account.nodeId}/img/$FILENAME").exists())
            assertTrue(Path("$FS_ROOT/${account.nodeId}/thumb/$FILENAME").exists())
        }

    @Test
    fun `throw on duplicate filename for account`(): Unit =
        runBlocking {
            fsAccess.saveNewImage(gibbonBytes, FILENAME, newImage)
            assertTrue(Path("$FS_ROOT/${account.nodeId}/img/$FILENAME").exists())

            assertFails {
                fsAccess.saveNewImage(gibbonBytes, FILENAME, newImage)
            }
        }

    @Test
    fun `test file loads from disk`() =
        runBlocking {
            fsAccess.saveNewImage(gibbonBytes, FILENAME, newImage)
            assertTrue(Path("$FS_ROOT/${account.nodeId}/img/$FILENAME").exists())
            assertTrue(Path("$FS_ROOT/${account.nodeId}/thumb/$FILENAME").exists())

            val img =
                fsAccess.loadImage(
                    uploaderAccountId = account.nodeId,
                    fileName = FILENAME,
                    isThumb = false,
                )

            assertTrue(img.exists())

            val thumb =
                fsAccess.loadImage(
                    uploaderAccountId = account.nodeId,
                    fileName = FILENAME,
                    isThumb = true,
                )

            assertTrue(thumb.exists())
        }
}
