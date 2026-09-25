package com.aryaxzell.aurabrowser.data.model

data class DownloadItem(
    val id: Long,
    val title: String,
    val description: String = "",
    val url: String = "",
    val status: DownloadStatus,
    val totalBytes: Long = 0,
    val downloadedBytes: Long = 0,
    val localUri: String? = null,
    val mimeType: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    val progress: Float
        get() = if (totalBytes > 0) (downloadedBytes.toFloat() / totalBytes).coerceIn(0f, 1f) else 0f

    val progressPercent: Int
        get() = (progress * 100).toInt()
}

enum class DownloadStatus {
    PENDING,
    RUNNING,
    PAUSED,
    SUCCESSFUL,
    FAILED
}
