package com.aryaxzell.aurabrowser.data.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream

data class WorkflowRunInfo(
    val id: Long,
    val runNumber: Int,
    val headSha: String,
    val title: String,
    val createdAt: String,
    val htmlUrl: String,
    val artifactsUrl: String
)

data class ArtifactInfo(
    val id: Long,
    val name: String,
    val architecture: String,
    val sizeInBytes: Long,
    val archiveDownloadUrl: String,
    val directDownloadUrls: List<String> = emptyList()
)

sealed class UpdateCheckResult {
    data class UpdateAvailable(
        val runInfo: WorkflowRunInfo,
        val artifact: ArtifactInfo?,
        val currentVersion: String,
        val newVersion: String,
        val deviceArchitecture: String
    ) : UpdateCheckResult()

    data class UpToDate(
        val currentVersion: String,
        val deviceArchitecture: String
    ) : UpdateCheckResult()

    data class Error(
        val message: String
    ) : UpdateCheckResult()
}

object AppUpdateManager {

    fun getDeviceArchitecture(): String {
        val supportedAbis = Build.SUPPORTED_ABIS ?: emptyArray()
        val primaryAbi = supportedAbis.firstOrNull()?.lowercase() ?: ""
        return when {
            primaryAbi.contains("arm64") -> "arm64-v8a"
            primaryAbi.contains("armeabi") || primaryAbi.contains("v7a") -> "armeabi-v7a"
            primaryAbi.contains("x86_64") -> "x86_64"
            primaryAbi.contains("x86") -> "x86"
            else -> "universal"
        }
    }

    fun getArchitectureLabel(abi: String): String {
        return when (abi) {
            "arm64-v8a" -> "ARM64 (64-bit)"
            "armeabi-v7a" -> "ARM32 (32-bit)"
            "x86_64" -> "x86_64 (64-bit)"
            "x86" -> "x86 (32-bit)"
            else -> "Universal"
        }
    }

    fun cleanUpdateArtifacts(context: Context) {
        try {
            val targetDir = File(context.cacheDir, "apks")
            if (targetDir.exists()) {
                targetDir.listFiles()?.forEach { file ->
                    try {
                        file.delete()
                    } catch (_: Exception) {}
                }
                try {
                    targetDir.delete()
                } catch (_: Exception) {}
            }
            context.cacheDir.listFiles()?.forEach { file ->
                if (file.name.endsWith(".apk", ignoreCase = true) || file.name.startsWith("aura_", ignoreCase = true)) {
                    try {
                        file.delete()
                    } catch (_: Exception) {}
                }
            }
        } catch (_: Exception) {}
    }

    private fun fetchReleaseAssets(deviceAbi: String): List<String> {
        val urls = mutableListOf<String>()
        try {
            val url = URL("https://api.github.com/repos/AryaXzell/Aura-Browser/releases/latest")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("User-Agent", "AuraBrowser-Android")
                setRequestProperty("Accept", "application/vnd.github+json")
            }
            if (conn.responseCode in 200..299) {
                val body = conn.inputStream.bufferedReader().use { it.readText() }
                val root = JSONObject(body)
                val assets = root.optJSONArray("assets")
                if (assets != null) {
                    val matchingUrls = mutableListOf<String>()
                    val universalUrls = mutableListOf<String>()
                    val otherUrls = mutableListOf<String>()
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        val name = asset.optString("name", "")
                        val dlUrl = asset.optString("browser_download_url", "")
                        if (dlUrl.isBlank() || !name.endsWith(".apk", ignoreCase = true)) continue

                        when {
                            deviceAbi == "arm64-v8a" && name.contains("arm64", ignoreCase = true) -> matchingUrls.add(dlUrl)
                            deviceAbi == "armeabi-v7a" && (name.contains("armeabi", ignoreCase = true) || name.contains("arm32", ignoreCase = true)) -> matchingUrls.add(dlUrl)
                            name.contains("universal", ignoreCase = true) -> universalUrls.add(dlUrl)
                            else -> otherUrls.add(dlUrl)
                        }
                    }
                    urls.addAll(matchingUrls)
                    urls.addAll(universalUrls)
                    urls.addAll(otherUrls)
                }
            }
        } catch (_: Exception) {}
        return urls
    }

    suspend fun checkForUpdate(
        context: Context,
        currentRunNumber: Int,
        githubToken: String? = null
    ): UpdateCheckResult = withContext(Dispatchers.IO) {
        try {
            val deviceAbi = getDeviceArchitecture()
            val deviceLabel = getArchitectureLabel(deviceAbi)

            val url = URL("https://api.github.com/repos/AryaXzell/Aura-Browser/actions/runs?status=success&per_page=5")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 12000
                readTimeout = 12000
                setRequestProperty("User-Agent", "AuraBrowser-Android")
                setRequestProperty("Accept", "application/vnd.github+json")
                if (!githubToken.isNullOrBlank()) {
                    setRequestProperty("Authorization", "Bearer $githubToken")
                }
            }

            if (conn.responseCode !in 200..299) {
                return@withContext UpdateCheckResult.Error("HTTP ${conn.responseCode}: ${conn.responseMessage}")
            }

            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val root = JSONObject(body)
            val runsArr = root.optJSONArray("workflow_runs")
            if (runsArr == null || runsArr.length() == 0) {
                return@withContext UpdateCheckResult.Error("Tidak ada riwayat workflow yang ditemukan")
            }

            val latest = runsArr.getJSONObject(0)
            val runId = latest.getLong("id")
            val runNumber = latest.getInt("run_number")
            val headSha = latest.optString("head_sha", "").take(7)
            val title = latest.optString("display_title", "Build #$runNumber")
            val createdAt = latest.optString("created_at", "")
            val htmlUrl = latest.optString("html_url", "https://github.com/AryaXzell/Aura-Browser/actions")
            val artifactsUrl = latest.optString("artifacts_url", "")

            val runInfo = WorkflowRunInfo(
                id = runId,
                runNumber = runNumber,
                headSha = headSha,
                title = title,
                createdAt = createdAt,
                htmlUrl = htmlUrl,
                artifactsUrl = artifactsUrl
            )

            if (runNumber <= currentRunNumber) {
                return@withContext UpdateCheckResult.UpToDate(
                    currentVersion = "Build #$currentRunNumber ($headSha)",
                    deviceArchitecture = deviceLabel
                )
            }

            // Dapatkan URL rilis langsung untuk bypass tanpa token/browser eksternal
            val candidateUrls = mutableListOf<String>()
            val releaseUrls = fetchReleaseAssets(deviceAbi)
            candidateUrls.addAll(releaseUrls)

            when (deviceAbi) {
                "arm64-v8a" -> {
                    candidateUrls.add("https://github.com/AryaXzell/Aura-Browser/releases/download/continuous/app-arm64-v8a-release.apk")
                    candidateUrls.add("https://github.com/AryaXzell/Aura-Browser/releases/latest/download/app-arm64-v8a-release.apk")
                    candidateUrls.add("https://github.com/AryaXzell/Aura-Browser/releases/download/continuous/app-universal-release.apk")
                    candidateUrls.add("https://github.com/AryaXzell/Aura-Browser/releases/latest/download/app-universal-release.apk")
                }
                "armeabi-v7a" -> {
                    candidateUrls.add("https://github.com/AryaXzell/Aura-Browser/releases/download/continuous/app-armeabi-v7a-release.apk")
                    candidateUrls.add("https://github.com/AryaXzell/Aura-Browser/releases/latest/download/app-armeabi-v7a-release.apk")
                    candidateUrls.add("https://github.com/AryaXzell/Aura-Browser/releases/download/continuous/app-universal-release.apk")
                    candidateUrls.add("https://github.com/AryaXzell/Aura-Browser/releases/latest/download/app-universal-release.apk")
                }
                else -> {
                    candidateUrls.add("https://github.com/AryaXzell/Aura-Browser/releases/download/continuous/app-universal-release.apk")
                    candidateUrls.add("https://github.com/AryaXzell/Aura-Browser/releases/latest/download/app-universal-release.apk")
                }
            }

            val artifact = fetchMatchingArtifact(artifactsUrl, deviceAbi, candidateUrls, githubToken)

            UpdateCheckResult.UpdateAvailable(
                runInfo = runInfo,
                artifact = artifact,
                currentVersion = "Build #$currentRunNumber",
                newVersion = "Build #$runNumber ($headSha)",
                deviceArchitecture = deviceLabel
            )
        } catch (e: Exception) {
            UpdateCheckResult.Error(e.localizedMessage ?: "Gagal memeriksa pembaruan")
        }
    }

    private fun fetchMatchingArtifact(
        artifactsUrl: String,
        deviceAbi: String,
        directUrls: List<String>,
        githubToken: String? = null
    ): ArtifactInfo? {
        val archLabel = getArchitectureLabel(deviceAbi)
        if (artifactsUrl.isBlank()) {
            return ArtifactInfo(
                id = 0L,
                name = "aura-browser-$deviceAbi-release",
                architecture = archLabel,
                sizeInBytes = 0L,
                archiveDownloadUrl = directUrls.firstOrNull() ?: "",
                directDownloadUrls = directUrls
            )
        }

        return try {
            val conn = (URL(artifactsUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("User-Agent", "AuraBrowser-Android")
                setRequestProperty("Accept", "application/vnd.github+json")
                if (!githubToken.isNullOrBlank()) {
                    setRequestProperty("Authorization", "Bearer $githubToken")
                }
            }
            if (conn.responseCode !in 200..299) {
                return ArtifactInfo(
                    id = 0L,
                    name = "aura-browser-$deviceAbi-release",
                    architecture = archLabel,
                    sizeInBytes = 0L,
                    archiveDownloadUrl = directUrls.firstOrNull() ?: "",
                    directDownloadUrls = directUrls
                )
            }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val root = JSONObject(body)
            val arr = root.optJSONArray("artifacts") ?: return null
            val artifacts = mutableListOf<ArtifactInfo>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val name = obj.getString("name")
                val arch = when {
                    name.contains("arm64", ignoreCase = true) -> "ARM64 (64-bit)"
                    name.contains("armeabi", ignoreCase = true) || name.contains("arm32", ignoreCase = true) -> "ARM32 (32-bit)"
                    name.contains("universal", ignoreCase = true) -> "Universal"
                    else -> "Universal"
                }
                val archiveUrl = obj.optString("archive_download_url", "")
                val mergedUrls = directUrls.toMutableList()
                // HANYA sertakan archiveUrl jika ada token. Tanpa token, request ke api.github.com akan selalu 401.
                if (!githubToken.isNullOrBlank() && archiveUrl.isNotBlank() && !mergedUrls.contains(archiveUrl)) {
                    mergedUrls.add(archiveUrl)
                }
                artifacts.add(
                    ArtifactInfo(
                        id = obj.getLong("id"),
                        name = name,
                        architecture = arch,
                        sizeInBytes = obj.optLong("size_in_bytes", 0),
                        archiveDownloadUrl = archiveUrl,
                        directDownloadUrls = mergedUrls
                    )
                )
            }

            // Seleksi cerdas sesuai arsitektur spesifik perangkat
            when (deviceAbi) {
                "arm64-v8a" -> {
                    artifacts.find { it.name.contains("arm64", ignoreCase = true) && it.name.contains("release", ignoreCase = true) }
                        ?: artifacts.find { it.name.contains("arm64", ignoreCase = true) }
                        ?: artifacts.find { it.name.contains("universal", ignoreCase = true) && it.name.contains("release", ignoreCase = true) }
                        ?: artifacts.find { it.name.contains("universal", ignoreCase = true) }
                }
                "armeabi-v7a" -> {
                    artifacts.find { (it.name.contains("armeabi", ignoreCase = true) || it.name.contains("arm32", ignoreCase = true)) && it.name.contains("release", ignoreCase = true) }
                        ?: artifacts.find { it.name.contains("armeabi", ignoreCase = true) || it.name.contains("arm32", ignoreCase = true) }
                        ?: artifacts.find { it.name.contains("universal", ignoreCase = true) && it.name.contains("release", ignoreCase = true) }
                        ?: artifacts.find { it.name.contains("universal", ignoreCase = true) }
                }
                else -> {
                    artifacts.find { it.name.contains("universal", ignoreCase = true) && it.name.contains("release", ignoreCase = true) }
                        ?: artifacts.find { it.name.contains("universal", ignoreCase = true) }
                        ?: artifacts.firstOrNull()
                }
            } ?: ArtifactInfo(
                id = 0L,
                name = "aura-browser-$deviceAbi-release",
                architecture = archLabel,
                sizeInBytes = 0L,
                archiveDownloadUrl = directUrls.firstOrNull() ?: "",
                directDownloadUrls = directUrls
            )
        } catch (e: Exception) {
            ArtifactInfo(
                id = 0L,
                name = "aura-browser-$deviceAbi-release",
                architecture = archLabel,
                sizeInBytes = 0L,
                archiveDownloadUrl = directUrls.firstOrNull() ?: "",
                directDownloadUrls = directUrls
            )
        }
    }

    suspend fun downloadExtractAndInstall(
        context: Context,
        downloadUrls: List<String>,
        githubToken: String? = null,
        onProgress: (Float) -> Unit,
        onStatusChange: (String) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        cleanUpdateArtifacts(context)
        val targetDir = File(context.cacheDir, "apks").apply { mkdirs() }

        var lastError: Exception? = null

        for (candidateUrl in downloadUrls) {
            try {
                // Lewati URL API jika token kosong untuk mencegah kode HTTP 401
                if (candidateUrl.startsWith("https://api.github.com/") && githubToken.isNullOrBlank()) {
                    continue
                }

                onStatusChange("Menghubungkan ke server unduhan...")
                val tempFile = File(targetDir, "aura_download_temp")
                if (tempFile.exists()) tempFile.delete()

                var currentUrl = candidateUrl
                var conn: HttpURLConnection
                var redirects = 0

                while (true) {
                    conn = (URL(currentUrl).openConnection() as HttpURLConnection).apply {
                        instanceFollowRedirects = false
                        connectTimeout = 15000
                        readTimeout = 30000
                        setRequestProperty("User-Agent", "AuraBrowser-Android")
                        setRequestProperty("Accept", "*/*")
                        // JANGAN kirim Authorization ke AWS S3 / objects.githubusercontent.com agar tidak 400 Bad Request
                        if (!githubToken.isNullOrBlank() && currentUrl.startsWith("https://api.github.com/")) {
                            setRequestProperty("Authorization", "Bearer $githubToken")
                        }
                    }

                    val status = conn.responseCode
                    if (status in 300..399) {
                        val location = conn.getHeaderField("Location")
                        conn.disconnect()
                        if (location != null && redirects++ < 5) {
                            currentUrl = location
                            continue
                        }
                    }
                    break
                }

                if (conn.responseCode !in 200..299) {
                    lastError = if (conn.responseCode == 401) {
                        Exception("Autentikasi diperlukan untuk mengunduh artefak mentah.")
                    } else if (conn.responseCode == 404) {
                        Exception("Paket instalasi pembaruan belum dipublikasikan di server rilis GitHub.")
                    } else {
                        Exception("Gagal mengunduh pembaruan (HTTP ${conn.responseCode})")
                    }
                    continue
                }

                onStatusChange("Mengunduh paket aplikasi...")
                val contentLength = conn.contentLength.toFloat()
                var downloaded = 0L

                conn.inputStream.use { input ->
                    tempFile.outputStream().use { output ->
                        val buffer = ByteArray(8192)
                        var bytes: Int
                        while (input.read(buffer).also { bytes = it } != -1) {
                            output.write(buffer, 0, bytes)
                            downloaded += bytes
                            if (contentLength > 0) {
                                onProgress((downloaded / contentLength).coerceIn(0f, 1f))
                            }
                        }
                    }
                }

                onProgress(1f)

                // Periksa apakah berkas berupa arsip ZIP yang berisi APK atau langsung berkas APK
                var containsNestedApk = false
                try {
                    ZipInputStream(FileInputStream(tempFile)).use { zipIn ->
                        var entry = zipIn.nextEntry
                        while (entry != null) {
                            if (!entry.isDirectory && entry.name.endsWith(".apk", ignoreCase = true)) {
                                containsNestedApk = true
                                break
                            }
                            zipIn.closeEntry()
                            entry = zipIn.nextEntry
                        }
                    }
                } catch (_: Exception) {
                    containsNestedApk = false
                }

                val finalApk = File(targetDir, "aura_update.apk")
                if (finalApk.exists()) finalApk.delete()

                if (containsNestedApk) {
                    onStatusChange("Mengekstrak berkas APK...")
                    ZipInputStream(FileInputStream(tempFile)).use { zipIn ->
                        var entry = zipIn.nextEntry
                        while (entry != null) {
                            if (!entry.isDirectory && entry.name.endsWith(".apk", ignoreCase = true)) {
                                finalApk.outputStream().use { apkOut ->
                                    zipIn.copyTo(apkOut)
                                }
                                zipIn.closeEntry()
                                break
                            }
                            zipIn.closeEntry()
                            entry = zipIn.nextEntry
                        }
                    }
                    try { tempFile.delete() } catch (_: Exception) {}
                } else {
                    tempFile.renameTo(finalApk)
                }

                if (!finalApk.exists() || finalApk.length() == 0L) {
                    lastError = Exception("Gagal menyiapkan file APK instalasi")
                    continue
                }

                onStatusChange("Menyiapkan instalasi...")
                return@withContext Result.success(finalApk)
            } catch (e: Exception) {
                lastError = e
            }
        }

        Result.failure(lastError ?: Exception("Gagal mengunduh pembaruan. Silakan coba lagi."))
    }

    fun launchApkInstaller(context: Context, apkFile: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                Toast.makeText(
                    context,
                    "Izinkan instalasi aplikasi dari sumber ini untuk melanjutkan pembaruan",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
        }

        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(installIntent)
    }
}
