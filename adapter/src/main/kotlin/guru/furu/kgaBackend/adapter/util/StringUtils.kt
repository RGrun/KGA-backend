package guru.furu.kgaBackend.adapter.util

import kotlin.random.Random

class StringUtils {
    companion object {
        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z')

        fun randomString(len: Int = 10): String =
            (1..len)
                .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
                .joinToString("")
    }
}
