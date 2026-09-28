package com.aryaxzell.aurabrowser.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val url: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val url: String,
    val visitedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "site_settings")
data class SiteSettingsEntity(
    @PrimaryKey
    val host: String, // Host without www., e.g., "example.com"
    val javascript: Int = 0, // 0: Default, 1: On, 2: Off
    val desktop: Int = 0,    // 0: Default, 1: On, 2: Off
    val adBlock: Int = 0     // 0: Default, 1: On, 2: Off
)
