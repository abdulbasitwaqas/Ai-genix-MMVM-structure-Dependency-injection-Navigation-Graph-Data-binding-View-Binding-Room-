package com.scope.smartdrivedemo

import android.content.Context
import android.util.Log
import org.joda.time.DateTime
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FileLoggingTree(
    private val context: Context,
    private val logHeader: String?
) : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, tag, message, t)
//        printInConsole(priority, tag, message, t)

        try {
            getLogDirectory(context)

            val logTimeStamp = SimpleDateFormat("MM dd HH:mm:ss:SSS", Locale.getDefault()).format(
                Date()
            )
            val file = File(context.cacheDir, LOG_DIRECTORY + File.separator + getLogFileNameForDate(
                Date()
            ))

            FileUtil.createParentDirectories(file)

            if (file.createNewFile()) {
                // writing log header if new file has been created
                if (file.exists() && logHeader != null) {
                    val fileOutputStream = FileOutputStream(file, true)
                    fileOutputStream.write(logHeader.toByteArray())
                    fileOutputStream.close()
                }
            }

            if (file.exists()) {
                val fileOutputStream = FileOutputStream(file, true)

                fileOutputStream.write("$logTimeStamp $tag: $message\n".toByteArray())
                fileOutputStream.close()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error while logging into file : $e")
        }
    }

    private fun printInConsole(priority: Int, tag: String?, message: String, t: Throwable?) {
        when (priority) {
            Log.DEBUG -> Log.d(tag, message, t)
            Log.INFO -> Log.i(tag, message, t)
            Log.ERROR -> Log.e(tag, message, t)
            Log.VERBOSE -> Log.v(tag, message, t)
            Log.WARN -> Log.w(tag, message, t)
        }
    }

    companion object {
        private val TAG = FileLoggingTree::class.java.simpleName
        const val LOG_DIRECTORY = "/ScopeTechnologyLogs"

        private fun getLogFileNameForDate(date: Date): String {
            return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date) + ".txt"
        }

        fun getLogDirectory(context: Context): File? {
            val directory = File(context.cacheDir, LOG_DIRECTORY)
            if (!directory.exists()) {
                if (!directory.mkdir()) {
                    return null
                }
            }
            return directory
        }

        fun checkForObsoleteLogFiles(context: Context) {
            val logDayCount = 7
            val directory = getLogDirectory(context) ?: return
            val files = FileUtil.getAllLogFiles(directory)

            if (logDayCount > 0) {
                for (file in files) {
                    var shouldDelete = true

                    for (i in 0 until logDayCount) {
                        val logfileName = getLogFileNameForDate(DateTime.now().minusDays(i).toDate())
                        if (file.name.equals(logfileName, ignoreCase = true)) {
                            shouldDelete = false
                            break
                        }
                    }

                    if (shouldDelete) {
                        val fileName = file.name
                        if (file.delete()) {
                            Timber.i("Obsolete log file deleted: $fileName")
                        }
                    }
                }
            }
        }
    }

}