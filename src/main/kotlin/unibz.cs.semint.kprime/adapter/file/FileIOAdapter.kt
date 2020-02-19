package unibz.cs.semint.kprime.adapter.file

import unibz.cs.semint.kprime.usecase.XPathTransformUseCase
import java.io.*
import java.nio.charset.StandardCharsets

class FileIOAdapter {
    fun workingFilePath(db: String): String {
        val fileName = "/home/nicola/Tmp/db.xml"
        val writer = BufferedWriter(FileWriter(fileName));
        writer.write(db);
        writer.close();
        return  fileName
    }

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

        fun inputStreamFromText(text:String):InputStream {
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