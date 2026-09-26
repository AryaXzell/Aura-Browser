package com.aryaxzell.aurabrowser.data.adblock

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Mesin pemblokiran ringan berbasis domain-suffix matching menggunakan HashSet.
 * Dirancang untuk kecepatan pencocokan O(1) rata-rata per level domain,
 * jauh lebih efisien dibanding linear scan array yang dipakai implementasi sebelumnya —
 * krusial karena shouldInterceptRequest dipanggil untuk SETIAP resource yang dimuat halaman.
 */
class AdBlockEngine(private val context: Context) {
    private val adDomains = HashSet<String>()
    private val trackerDomains = HashSet<String>()
    @Volatile private var isLoaded = false

    fun loadIfNeeded() {
        if (isLoaded) return
        synchronized(this) {
            if (isLoaded) return
            loadDomainSet("ads", "blocklist_ads.txt", adDomains)
            loadDomainSet("trackers", "blocklist_trackers.txt", trackerDomains)
            isLoaded = true
        }
    }

    private fun loadDomainSet(cacheKey: String, assetFileName: String, target: HashSet<String>) {
        val cacheFile = java.io.File(context.filesDir, "adblock_cache_$cacheKey.txt")
        val source = if (cacheFile.exists() && cacheFile.length() > 0) {
            try {
                cacheFile.inputStream()
            } catch (e: Exception) {
                context.assets.open(assetFileName)
            }
        } else {
            try {
                context.assets.open(assetFileName)
            } catch (e: Exception) {
                null
            }
        }

        if (source == null) return

        try {
            source.use { input ->
                BufferedReader(InputStreamReader(input)).use { reader ->
                    reader.forEachLine { line ->
                        val trimmed = line.trim().lowercase()
                        if (trimmed.isNotBlank() && !trimmed.startsWith("#")) {
                            target.add(trimmed)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // fail-safe, biarkan set kosong untuk kategori ini jika kedua sumber gagal
        }
    }

    /**
     * Mengecek apakah host termasuk domain iklan yang harus diblokir.
     * Menggunakan domain-suffix matching: "ads.example.com" akan match terhadap
     * entry "example.com" di daftar, TAPI TIDAK sebaliknya — mencegah false-positive
     * seperti "notexample.com" ikut ter-match oleh entry "example.com".
     */
    fun isAdDomain(host: String): Boolean = matchesAnySuffix(host, adDomains)

    fun isTrackerDomain(host: String): Boolean = matchesAnySuffix(host, trackerDomains)

    private fun matchesAnySuffix(host: String, domainSet: HashSet<String>): Boolean {
        if (domainSet.isEmpty()) return false
        val lowerHost = host.lowercase()
        // Cek exact match dulu (paling cepat, O(1))
        if (domainSet.contains(lowerHost)) return true
        // Cek domain-suffix: pecah host jadi subdomain bertingkat dan cek tiap level
        var remaining = lowerHost
        while (true) {
            val dotIndex = remaining.indexOf('.')
            if (dotIndex == -1) break
            remaining = remaining.substring(dotIndex + 1)
            if (domainSet.contains(remaining)) return true
        }
        return false
    }

    /**
     * Memperbarui filter list dari URL remote. Dipanggil secara periodik (misalnya
     * sekali per hari) dari background, TIDAK PERNAH dari jalur startup app.
     */
    suspend fun updateFromRemote(adsUrl: String, trackersUrl: String) {
        withContext(Dispatchers.IO) {
            try {
                downloadToCache(adsUrl, "adblock_cache_ads.txt")
                downloadToCache(trackersUrl, "adblock_cache_trackers.txt")
            } catch (e: Exception) {
                // Gagal update — bundled asset atau cache lama tetap dipakai, tidak masalah
            }
        }
    }

    private fun downloadToCache(urlString: String, cacheFileName: String) {
        val connection = java.net.URL(urlString).openConnection() as java.net.HttpURLConnection
        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        connection.inputStream.use { input ->
            val cacheFile = java.io.File(context.filesDir, cacheFileName)
            val tempFile = java.io.File(context.filesDir, "$cacheFileName.tmp")
            tempFile.outputStream().use { output -> input.copyTo(output) }
            tempFile.renameTo(cacheFile) // atomic replace agar tidak ada file corrupt jika proses terputus
        }
    }
}
