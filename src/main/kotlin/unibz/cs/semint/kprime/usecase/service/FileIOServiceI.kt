package unibz.cs.semint.kprime.usecase.service

import unibz.cs.semint.kprime.usecase.common.XPathTransformUseCase
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets

interface FileIOServiceI {
    fun writeOnWorkingFilePath(db: String, fileName: String): String

    companion object {

        fun readString(inputStream: InputStream): String {
            val result = ByteArrayOutputStream()
            var buffer = ByteArray(1024)
            var length = inputStream.read(buffer)
            while (length != -1) {
                result.write(buffer, 0, length)
                length = inputStream.read(buffer)
            }
            return result.toString("UTF-8")
        }

        fun inputStreamFromText(text:String): InputStream {
            return ByteArrayInputStream(text.toByteArray(StandardCharsets.UTF_8))
        }

        fun inputStreamFromPath(dbFilePath: String): InputStream {
            var dbInputStream: InputStream
            if (dbFilePath.startsWith("/"))
                dbInputStream = FileInputStream(dbFilePath)
            else
                dbInputStream = XPathTransformUseCase::class.java.getResourceAsStream("/${dbFilePath}")
            return dbInputStream
        }

    }

}
