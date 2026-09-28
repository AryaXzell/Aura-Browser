package com.aryaxzell.aurabrowser.data.model

enum class ConsoleLevel {
    LOG, WARNING, ERROR, DEBUG
}

data class ConsoleLogEntry(
    val id: Long = System.nanoTime(),
    val timestamp: Long = System.currentTimeMillis(),
    val level: ConsoleLevel,
    val message: String,
    val sourceId: String,
    val lineNumber: Int
)
