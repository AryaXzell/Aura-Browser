package com.aryaxzell.aurabrowser.data.util

import android.content.Context
import android.content.Intent

object ShareUtil {
    fun shareUrl(context: Context, url: String, title: String? = null) {
        if (url.isBlank()) return
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, url)
                if (!title.isNullOrBlank()) {
                    putExtra(Intent.EXTRA_SUBJECT, title)
                }
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share URL"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
