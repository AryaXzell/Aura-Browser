package com.aryaxzell.aurabrowser.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BrowserDao {
    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE url = :url")
    suspend fun deleteBookmarkByUrl(url: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE url = :url)")
    fun isBookmarked(url: String): Flow<Boolean>

    // History
    @Query("SELECT * FROM history ORDER BY visitedAt DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE title LIKE '%' || :query || '%' OR url LIKE '%' || :query || '%' ORDER BY visitedAt DESC")
    fun searchHistory(query: String): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    @Delete
    suspend fun deleteHistory(history: HistoryEntity)

    @Query("DELETE FROM history")
    suspend fun clearAllHistory()
}
