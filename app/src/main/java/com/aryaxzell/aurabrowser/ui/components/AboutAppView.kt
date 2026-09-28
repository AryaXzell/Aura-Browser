package com.aryaxzell.aurabrowser.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.aryaxzell.aurabrowser.BuildConfig
import com.aryaxzell.aurabrowser.data.localization.AppStrings
import com.aryaxzell.aurabrowser.data.model.AppLanguage
import com.aryaxzell.aurabrowser.data.model.WhatsNewData
import com.aryaxzell.aurabrowser.data.preferences.BrowserPreferences
import com.aryaxzell.aurabrowser.data.update.AppUpdateManager
import com.aryaxzell.aurabrowser.data.update.UpdateCheckResult
import com.aryaxzell.aurabrowser.ui.util.iosPressEffect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppView(
    currentLanguage: AppLanguage,
    isDeveloperMode: Boolean,
    onSetDeveloperMode: (Boolean) -> Unit,
    devTapCount: Int,
    onDevTapCountChange: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = remember(currentLanguage) { AppStrings(currentLanguage) }
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val prefs = remember { BrowserPreferences(context) }

    var showChangelogDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    // Update States
    var isCheckingUpdate by remember { mutableStateOf(false) }
    var updateResult by remember { mutableStateOf<UpdateCheckResult?>(null) }
    var showUpdateModal by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var downloadStatus by remember { mutableStateOf("") }
    var downloadErrorMessage by remember { mutableStateOf<String?>(null) }

    // Periodic / on-mount check for updates
    LaunchedEffect(Unit) {
        val res = AppUpdateManager.checkForUpdate(
            context = context,
            currentRunNumber = prefs.installedRunNumber,
            githubToken = prefs.githubToken.ifBlank { null }
        )
        if (res is UpdateCheckResult.UpdateAvailable) {
            updateResult = res
            showUpdateModal = true
        }
    }

    val privacyContent = remember(currentLanguage) {
        when (currentLanguage) {
            AppLanguage.ID -> """
                Kebijakan Privasi Aura Browser
                
                Privasi Anda adalah prioritas utama kami. Aura Browser dirancang untuk melindungi aktivitas penjelajahan Anda:
                
                1. Tanpa Pengumpulan Data
                Kami tidak mengumpulkan, menyimpan, atau melacak riwayat penjelajahan, kata sandi, cookie, atau data pribadi Anda. Semua data tetap berada di perangkat Anda secara lokal.
                
                2. Pemblokiran Iklan & Pelacak Lokal
                Mesin pemblokir iklan dan pencegah pelacak kami berjalan sepenuhnya di perangkat Anda secara lokal tanpa mengirimkan lalu lintas data ke server pihak ketiga mana pun.
                
                3. Penyimpanan Aman
                Riwayat, Markah Buku, dan Data Kredensial disimpan dengan aman di dalam ruang sandbox aplikasi Android Anda dan tidak dapat diakses oleh aplikasi lain.
            """.trimIndent()
            AppLanguage.RU -> """
                Политика конфиденциальности Aura Browser
                
                Ваша конфиденциальность — наш главный приоритет. Aura Browser разработан для защиты вашей активности:
                
                1. Без сбора данных
                Мы не собираем, не храним и не отслеживаем вашу историю посещений, пароли, файлы cookie или личные данные. Все данные остаются на вашем устройстве.
                
                2. Локальная блокировка рекламы
                Наш движок блокировки рекламы и трекеров работает локально на вашем устройстве, не отправляя ваш трафик сторонним серверам.
                
                3. Безопасное хранение
                История, закладки и учетные данные надежно хранятся в песочнице вашего приложения Android и недоступны для других приложений.
            """.trimIndent()
            else -> """
                Aura Browser Privacy Policy
                
                Your privacy is our highest priority. Aura Browser is designed to safeguard your browsing activities:
                
                1. No Data Collection
                We do not collect, store, or track your browsing history, passwords, cookies, or personal information. All your browsing data remains entirely on your local device.
                
                2. Local Ad & Tracker Blocking
                Our ad blocking and tracker prevention engines run fully locally on your device without transmitting any of your traffic to third-party servers.
                
                3. Secure Sandboxed Storage
                Your History, Bookmarks, and Credentials are saved securely within the sandbox of your Android device, isolated from other applications.
            """.trimIndent()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Header Bar (About & Back Arrow)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.iosPressEffect()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.back,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.aboutTitle,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. App Header Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        // Logo Box
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                                        )
                                    )
                                )
                                .border(
                                    BorderStroke(
                                        0.5.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                    ),
                                    shape = RoundedCornerShape(22.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AuraVectorLogo(modifier = Modifier.size(54.dp))
                        }

                        // App Name, Slogan & Version info
                        Column {
                            Text(
                                text = "Aura Browser",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 24.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = strings.aboutSlogan,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // 2. Intro Description Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = strings.aboutIntroText,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 22.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Horizontally wrapping Badges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AboutBadge(
                                    icon = Icons.Default.FlashOn,
                                    text = strings.aboutBadgeFast,
                                    tint = Color(0xFF007AFF)
                                )
                                AboutBadge(
                                    icon = Icons.Default.Shield,
                                    text = strings.aboutBadgePrivate,
                                    tint = Color(0xFF34C759)
                                )
                                AboutBadge(
                                    icon = Icons.Default.Code,
                                    text = strings.aboutBadgeOpenSource,
                                    tint = Color(0xFF5856D6)
                                )
                                AboutBadge(
                                    icon = Icons.Default.AutoAwesome,
                                    text = strings.aboutBadgeModern,
                                    tint = Color(0xFFFF9500)
                                )
                            }
                        }
                    }

                    // 3. Main Action Lists (iOS Card Style)
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                    ) {
                        Column {
                            // Check for updates item
                            AboutRowItem(
                                icon = Icons.Default.SystemUpdate,
                                iconBg = Color(0xFF007AFF),
                                title = strings.aboutUpdateTitle,
                                subtitle = if (isCheckingUpdate) strings.aboutCheckingUpdate else strings.aboutUpdateDesc,
                                onClick = {
                                    if (!isCheckingUpdate && !isDownloading) {
                                        scope.launch {
                                            isCheckingUpdate = true
                                            downloadErrorMessage = null
                                            val res = AppUpdateManager.checkForUpdate(
                                                context = context,
                                                currentRunNumber = prefs.installedRunNumber,
                                                githubToken = prefs.githubToken.ifBlank { null }
                                            )
                                            isCheckingUpdate = false
                                            updateResult = res
                                            showUpdateModal = true
                                        }
                                    }
                                }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 54.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                            )
                            AboutRowItem(
                                icon = Icons.Default.ListAlt,
                                iconBg = Color(0xFF5856D6),
                                title = strings.aboutChangelogTitle,
                                subtitle = strings.aboutChangelogDesc,
                                onClick = {
                                    showChangelogDialog = true
                                }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 54.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                            )
                            AboutRowItem(
                                icon = Icons.Default.Code,
                                iconBg = Color(0xFF24292F),
                                title = strings.aboutSourceCodeTitle,
                                subtitle = strings.aboutSourceCodeDesc,
                                onClick = {
                                    try {
                                        uriHandler.openUri("https://github.com/AryaXzell/Aura-Browser")
                                    } catch (e: Exception) {}
                                }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 54.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                            )
                            AboutRowItem(
                                icon = Icons.Default.Security,
                                iconBg = Color(0xFF34C759),
                                title = strings.aboutPrivacyTitle,
                                subtitle = strings.aboutPrivacyDesc,
                                onClick = {
                                    showPrivacyDialog = true
                                }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 54.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                            )
                            AboutRowItem(
                                icon = Icons.Default.Mail,
                                iconBg = Color(0xFF007AFF),
                                title = strings.aboutSupportTitle,
                                subtitle = strings.aboutSupportDesc,
                                onClick = {
                                    try {
                                        uriHandler.openUri("https://github.com/AryaXzell/Aura-Browser/issues")
                                    } catch (e: Exception) {}
                                }
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 54.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                            )
                            // Migrated Developer Tap Info Row!
                            AboutRowItem(
                                icon = Icons.Default.Settings,
                                iconBg = Color(0xFF8E8E93),
                                title = "Developer Tap Info",
                                subtitle = if (isDeveloperMode) "You are already a developer" else "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                                onClick = {
                                    if (isDeveloperMode) {
                                        Toast.makeText(context, "No need, you are already a developer.", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val newCount = devTapCount + 1
                                        onDevTapCountChange(newCount)
                                        val remaining = 7 - newCount
                                        if (remaining in 1..4) {
                                            val stepText = if (remaining == 1) "step" else "steps"
                                            Toast.makeText(context, "You are now $remaining $stepText away from being a developer.", Toast.LENGTH_SHORT).show()
                                        } else if (remaining <= 0) {
                                            Toast.makeText(context, "You are now a developer!", Toast.LENGTH_SHORT).show()
                                            onSetDeveloperMode(true)
                                            onDevTapCountChange(0)
                                        }
                                    }
                                }
                            )
                        }
                    }

                    // 4. GitHub Profile Button with Avatar Preview
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                try {
                                    uriHandler.openUri("https://github.com/AryaXzell")
                                } catch (e: Exception) {}
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            AsyncImage(
                                model = "https://github.com/AryaXzell.png",
                                contentDescription = "AryaXzell GitHub Profile",
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                                contentScale = ContentScale.Crop
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = strings.aboutPassionTitle,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = when (currentLanguage) {
                                        AppLanguage.ID -> "Kunjungi profil GitHub AryaXzell"
                                        AppLanguage.RU -> "Посетить профиль GitHub AryaXzell"
                                        else -> "Visit AryaXzell's GitHub profile"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 5. Centered Footer
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Aura Browser",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "© 2026 AryaXzell. All rights reserved.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        )
                    }
                }
            }
        }
    }

    // Changelog dialog
    if (showChangelogDialog) {
        AlertDialog(
            onDismissRequest = { showChangelogDialog = false },
            title = {
                Text(
                    text = strings.aboutChangelogTitle,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                val entries = WhatsNewData.entries
                Column(
                    modifier = Modifier
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    entries.forEach { entry ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = entry.icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = entry.title,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = entry.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showChangelogDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Privacy policy dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Text(
                    text = strings.aboutPrivacyTitle,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = privacyContent,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // iOS Style Update Modal
    if (showUpdateModal && updateResult != null) {
        when (val res = updateResult!!) {
            is UpdateCheckResult.UpToDate -> {
                IOSAlertModal(
                    title = strings.aboutTitle,
                    message = "${strings.aboutUpToDateMessage}\n(${res.deviceArchitecture})",
                    onDismissRequest = { showUpdateModal = false }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable { showUpdateModal = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "OK",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            is UpdateCheckResult.UpdateAvailable -> {
                val archLabel = res.artifact?.architecture ?: res.deviceArchitecture
                IOSAlertModal(
                    title = "Pembaruan Tersedia",
                    message = if (downloadErrorMessage != null) {
                        downloadErrorMessage!!
                    } else if (isDownloading) {
                        downloadStatus
                    } else {
                        "${strings.aboutDownloadPrompt}\n\n${res.newVersion} [$archLabel]\n${res.runInfo.title}"
                    },
                    onDismissRequest = {
                        if (!isDownloading) showUpdateModal = false
                    },
                    content = if (isDownloading) {
                        {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (downloadProgress > 0f) {
                                    LinearProgressIndicator(
                                        progress = { downloadProgress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "${(downloadProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                                }
                            }
                        }
                    } else null
                ) {
                    if (isDownloading) {
                        // While downloading, show non-clickable status
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Mengunduh...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    } else if (downloadErrorMessage != null) {
                        // Error handling: Tetap di dalam aplikasi tanpa membuka browser eksternal
                        Row(modifier = Modifier.fillMaxWidth().height(48.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable {
                                        showUpdateModal = false
                                        downloadErrorMessage = null
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Tutup",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            VerticalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable {
                                        downloadErrorMessage = null
                                        val candidateUrls = res.artifact?.directDownloadUrls ?: emptyList()
                                        val urls = if (candidateUrls.isNotEmpty()) candidateUrls else listOf(
                                            "https://github.com/AryaXzell/Aura-Browser/releases/download/continuous/app-universal-release.apk"
                                        )
                                        scope.launch {
                                            isDownloading = true
                                            downloadProgress = 0f
                                            downloadStatus = "Menghubungkan ke server unduhan..."
                                            val dlResult = AppUpdateManager.downloadExtractAndInstall(
                                                context = context,
                                                downloadUrls = urls,
                                                githubToken = prefs.githubToken.ifBlank { null },
                                                onProgress = { downloadProgress = it },
                                                onStatusChange = { downloadStatus = it }
                                            )
                                            isDownloading = false
                                            dlResult.onSuccess { apkFile ->
                                                prefs.installedRunNumber = res.runInfo.runNumber
                                                showUpdateModal = false
                                                AppUpdateManager.launchApkInstaller(context, apkFile)
                                            }.onFailure { err ->
                                                downloadErrorMessage = err.localizedMessage ?: "Gagal mengunduh berkas pembaruan"
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Coba Lagi",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    } else {
                        // Normal prompt: Batal vs Download (100% in-app)
                        Row(modifier = Modifier.fillMaxWidth().height(48.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable { showUpdateModal = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Batal",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            VerticalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable {
                                        val candidateUrls = res.artifact?.directDownloadUrls ?: emptyList()
                                        val urls = if (candidateUrls.isNotEmpty()) candidateUrls else listOf(
                                            "https://github.com/AryaXzell/Aura-Browser/releases/download/continuous/app-universal-release.apk"
                                        )
                                        scope.launch {
                                            isDownloading = true
                                            downloadProgress = 0f
                                            downloadStatus = "Menghubungkan ke server unduhan..."
                                            val dlResult = AppUpdateManager.downloadExtractAndInstall(
                                                context = context,
                                                downloadUrls = urls,
                                                githubToken = prefs.githubToken.ifBlank { null },
                                                onProgress = { downloadProgress = it },
                                                onStatusChange = { downloadStatus = it }
                                            )
                                            isDownloading = false
                                            dlResult.onSuccess { apkFile ->
                                                prefs.installedRunNumber = res.runInfo.runNumber
                                                showUpdateModal = false
                                                AppUpdateManager.launchApkInstaller(context, apkFile)
                                            }.onFailure { err ->
                                                downloadErrorMessage = err.localizedMessage ?: "Gagal mengunduh berkas pembaruan"
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Download",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            is UpdateCheckResult.Error -> {
                IOSAlertModal(
                    title = "Pemeriksaan Gagal",
                    message = res.message,
                    onDismissRequest = { showUpdateModal = false }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable { showUpdateModal = false },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tutup",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IOSAlertModal(
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    content: (@Composable () -> Unit)? = null,
    buttons: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .fillMaxWidth(0.85f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        lineHeight = 19.sp
                    ),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                if (content != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                        content()
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                buttons()
            }
        }
    }
}

@Composable
private fun AboutBadge(
    icon: ImageVector,
    text: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = tint.copy(alpha = 0.08f),
        border = BorderStroke(0.5.dp, tint.copy(alpha = 0.12f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = tint
            )
        }
    }
}

@Composable
private fun AboutRowItem(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
    }
}
