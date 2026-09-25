package com.aryaxzell.aurabrowser.data.download

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.aryaxzell.aurabrowser.data.model.DownloadItem
import com.aryaxzell.aurabrowser.data.model.DownloadStatus
import java.util.Locale

class DownloadTracker(private val context: Context) {
    private val downloadManager: DownloadManager? =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager

    fun getDownloads(): List<DownloadItem> {
        val dm = downloadManager ?: return emptyList()
        val list = mutableListOf<DownloadItem>()
        val query = DownloadManager.Query()
        val cursor = try {
            dm.query(query)
        } catch (e: Exception) {
            null
        }

        cursor?.use { c ->
            val idCol = c.getColumnIndex(DownloadManager.COLUMN_ID)
            val titleCol = c.getColumnIndex(DownloadManager.COLUMN_TITLE)
            val descCol = c.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION)
            val uriCol = c.getColumnIndex(DownloadManager.COLUMN_URI)
            val statusCol = c.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val totalCol = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
            val downloadedCol = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
            val localUriCol = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
            val mediaTypeCol = c.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE)
            val timeCol = c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)

            while (c.moveToNext()) {
                val id = if (idCol >= 0) c.getLong(idCol) else 0L
                val title = (if (titleCol >= 0) c.getString(titleCol) else null) ?: "File_$id"
                val desc = if (descCol >= 0) c.getString(descCol) ?: "" else ""
                val uri = if (uriCol >= 0) c.getString(uriCol) ?: "" else ""
                val statusInt = if (statusCol >= 0) c.getInt(statusCol) else DownloadManager.STATUS_FAILED
                val total = if (totalCol >= 0) c.getLong(totalCol) else 0L
                val downloaded = if (downloadedCol >= 0) c.getLong(downloadedCol) else 0L
                val localUri = if (localUriCol >= 0) c.getString(localUriCol) else null
                val mediaType = if (mediaTypeCol >= 0) c.getString(mediaTypeCol) else null
                val time = if (timeCol >= 0) c.getLong(timeCol) else System.currentTimeMillis()

                val status = when (statusInt) {
                    DownloadManager.STATUS_PENDING -> DownloadStatus.PENDING
                    DownloadManager.STATUS_RUNNING -> DownloadStatus.RUNNING
                    DownloadManager.STATUS_PAUSED -> DownloadStatus.PAUSED
                    DownloadManager.STATUS_SUCCESSFUL -> DownloadStatus.SUCCESSFUL
                    else -> DownloadStatus.FAILED
                }

                list.add(
                    DownloadItem(
                        id = id,
                        title = title,
                        description = desc,
                        url = uri,
                        status = status,
                        totalBytes = total,
                        downloadedBytes = downloaded,
                        localUri = localUri,
                        mimeType = mediaType,
                        timestamp = time
                    )
                )
            }
        }
        return list.sortedByDescending { it.timestamp }
    }

    fun removeDownload(id: Long) {
        try {
            downloadManager?.remove(id)
        } catch (_: Exception) {
        }
    }

    companion object {
        fun openFile(context: Context, item: DownloadItem) {
            try {
                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                val uri = dm?.getUriForDownloadedFile(item.id)
                    ?: item.localUri?.let { Uri.parse(it) }

                if (uri != null) {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, item.mimeType ?: "*/*")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(Intent.createChooser(intent, "Open with"))
                } else {
                    Toast.makeText(context, "File is not accessible", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "No app available to open this file", Toast.LENGTH_SHORT).show()
            }
        }

        fun shareFile(context: Context, item: DownloadItem) {
            try {
                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                val uri = dm?.getUriForDownloadedFile(item.id)
                    ?: item.localUri?.let { Uri.parse(it) }

                if (uri != null) {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = item.mimeType ?: "*/*"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share file"))
                } else {
                    Toast.makeText(context, "File is not accessible to share", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Unable to share file", Toast.LENGTH_SHORT).show()
            }
        }

        fun formatBytes(bytes: Long): String {
            if (bytes <= 0) return "0 B"
            val kb = bytes / 1024.0
            val mb = kb / 1024.0
            val gb = mb / 1024.0
            return when {
                gb >= 1.0 -> String.format(Locale.US, "%.1f GB", gb)
                mb >= 1.0 -> String.format(Locale.US, "%.1f MB", mb)
                kb >= 1.0 -> String.format(Locale.US, "%.1f KB", kb)
                else -> "$bytes B"
            }
        }
    }
}
