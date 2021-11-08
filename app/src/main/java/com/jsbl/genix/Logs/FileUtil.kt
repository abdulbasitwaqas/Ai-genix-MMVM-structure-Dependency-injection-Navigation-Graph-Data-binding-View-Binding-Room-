package com.scope.smartdrivedemo

import org.joda.time.DateTime
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object FileUtil {

    private const val BUFFER_SIZE = 1024

    fun createParentDirectories(inFile: File) {
        val parentDir = inFile.parentFile

        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs()
        }
    }

    fun getAllLogFiles(
        parentDir: File,
        recursively: Boolean = false
    ): List<File> {
        val inFiles = ArrayList<File>()

        parentDir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                if (recursively) {
                    inFiles.addAll(getAllLogFiles(file))
                }
            } else {
                inFiles.add(file)
            }
        }

        return inFiles
    }

    fun getTodaysLogFile(
        parentDir: File
    ): List<File> {
        val inFiles = ArrayList<File>()
        val date = DateTime.now().toString("dd-MM-YYYY")

        parentDir.listFiles()?.forEach { file ->
            if (file.isFile) {
                val fileName = file.nameWithoutExtension

                if (fileName == date) {
                    inFiles.add(file)
                }
            }
        }

        return inFiles
    }

    fun writeZipFile(files: List<File>, zipFile: String) {
        val out = ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile)))
        out.use { out ->
            val data = ByteArray(BUFFER_SIZE)
            files.forEach { file ->
                val fi = FileInputStream(file.absolutePath)
                val origin = BufferedInputStream(fi, BUFFER_SIZE)
                origin.use {
                    val entry = ZipEntry(file.name)
                    out.putNextEntry(entry)
                    var count: Int
                    count = origin.read(data, 0, BUFFER_SIZE)
                    while (count != -1) {
                        out.write(data, 0, count)
                        count = origin.read(data, 0, BUFFER_SIZE)
                    }
                }
            }
        }
    }
}