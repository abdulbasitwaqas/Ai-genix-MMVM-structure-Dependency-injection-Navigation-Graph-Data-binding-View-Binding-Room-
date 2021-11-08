package com.jsbl.genix.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.loader.content.CursorLoader


object RealPathUtil {
    @SuppressLint("NewApi")
    fun getRealPathFromURI_API19(context: Context, uri: Uri): String {
        uri.getPath()?.let { Log.e("uri", it) }
        var filePath = ""
        if (DocumentsContract.isDocumentUri(context, uri)) {
            val wholeID = DocumentsContract.getDocumentId(uri)
            Log.e("wholeID", wholeID)
            // Split at colon, use second item in the array
            val splits = wholeID.split(":").toTypedArray()
            if (splits.size == 2) {
                val id = splits[1]
                val column =
                    arrayOf(MediaStore.Images.Media.DATA)
                // where id is equal to
                val sel = MediaStore.Images.Media._ID + "=?"
                val cursor: Cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, arrayOf(id), null
                )!!
                val columnIndex: Int = cursor.getColumnIndex(column[0])
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex)
                }
                cursor.close()
            }
        } else {
            filePath = uri.getPath()!!
        }
        return filePath
    }

    @SuppressLint("NewApi")
    fun getRealPathFromURI_API11to18(context: Context?, contentUri: Uri?): String? {
        val proj =
            arrayOf(MediaStore.Images.Media.DATA)
        var result: String? = null
        val cursorLoader = CursorLoader(
            context!!,
            contentUri!!, proj, null, null, null
        )
        val cursor: Cursor = cursorLoader.loadInBackground()!!
        if (cursor != null) {
            val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            result = cursor.getString(column_index)
        }
        return result
    }

    fun getRealPathFromURI_BelowAPI11(context: Context, contentUri: Uri?): String {
        val proj =
            arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor =
            context.getContentResolver().query(contentUri!!, proj, null, null, null)!!
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    /**
     * helper to retrieve the path of an image URI
     */
    fun getPath(activity: Activity,uri: Uri?): String? {
        // just some safety built in
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = activity.managedQuery(uri, projection, null, null, null)
        if (cursor != null) {
            val column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val path = cursor.getString(column_index)
            cursor.close()
            return path
        }
        // this is our fallback here
        return uri.path
    }

}