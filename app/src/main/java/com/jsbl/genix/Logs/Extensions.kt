package com.jsbl.genix.Logs

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.jsbl.genix.BuildConfig
import com.scope.smartdrivedemo.FileLoggingTree
import com.scope.smartdrivedemo.FileLoggingTree.Companion.LOG_DIRECTORY
import com.scope.smartdrivedemo.FileUtil
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.io.File

fun sendLogFiles(
    activity: Activity,
    sendTodaysLogFile: Boolean = false
) {
    val logFileName = "AiGenixxLogs"

    if (!activity.isFinishing) {
        val directory = com.scope.smartdrivedemo.FileLoggingTree.getLogDirectory(activity)
        val mailTo = ""

        if (directory != null) {
            val df = DateTimeFormat.forPattern("yyyy-MM-dd-HH-mm-ss")

            val files = if (sendTodaysLogFile) {
                FileUtil.getTodaysLogFile(directory)
            } else {
                FileUtil.getAllLogFiles(directory)
            }

            val allZipFiles = files.filter { it.name.contains(".zip") }
            val allTxtFiles = files.filter { it.name.contains(".txt") }

            /* Delete all old zip files */
            if (allZipFiles.isNotEmpty()) {
                allZipFiles.filter {
                    it.delete()
                }
            }

            if (allTxtFiles.isNotEmpty()) {
                val zipFileName = "${logFileName }_${df.print(DateTime.now())}"
                val zipFile = File(
                    activity.cacheDir,
                    "${FileLoggingTree.LOG_DIRECTORY}/${zipFileName}.zip"
                )

                FileUtil.writeZipFile(allTxtFiles, zipFile.absolutePath)

                val intentBuilder = ShareCompat.IntentBuilder.from(activity)
                    .setType("message/rfc822")

                val authority = BuildConfig.APPLICATION_ID
                val content = FileProvider.getUriForFile(
                    activity.applicationContext,
                    authority,
                    zipFile
                )

                with(intentBuilder) {
                    setSubject("Ai-Genix")
                    setEmailTo(arrayOf(mailTo))
                    setStream(content)
                }

                val intent = intentBuilder.createChooserIntent()
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                if (intent.resolveActivity(activity.packageManager) != null) {
                    activity.startActivity(intent)
                } else {
                    Toast.makeText(activity, "Failed to send log.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}