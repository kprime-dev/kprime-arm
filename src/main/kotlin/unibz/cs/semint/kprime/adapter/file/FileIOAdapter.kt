package unibz.cs.semint.kprime.adapter.file

import unibz.cs.semint.kprime.usecase.service.FileIOServiceI
import java.io.*


class FileIOAdapter : FileIOServiceI {
    override fun writeOnWorkingFilePath(db: String, fileName: String): String {
        val writer = BufferedWriter(FileWriter(fileName));
        writer.write(db);
        writer.close();
        return fileName
    }

}