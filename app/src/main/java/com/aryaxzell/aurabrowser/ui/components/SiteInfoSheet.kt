package com.aryaxzell.aurabrowser.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.webkit.CookieManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aryaxzell.aurabrowser.data.db.SiteSettingsEntity
import com.aryaxzell.aurabrowser.data.model.SecurityInfo
import com.aryaxzell.aurabrowser.data.model.SecurityStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteInfoSheet(
    url: String,
    securityInfo: SecurityInfo?,
    siteSettings: SiteSettingsEntity?,
    onUpdateSiteSettings: (javascript: Int, desktop: Int, adBlock: Int) -> Unit,
    onResetSiteSettings: () -> Unit,
    onReloadPage: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val host = remember(url) {
        try {
            val uri = android.net.Uri.parse(url)
            uri.host?.removePrefix("www.") ?: url
        } catch (e: Exception) {
            url
        }
    }

    // Parse cookies for host/URL
    val cookiesList = remember(url) {
        val rawCookies = CookieManager.getInstance().getCookie(url)
        if (rawCookies.isNullOrBlank()) {
            emptyList()
        } else {
            rawCookies.split(";").mapNotNull {
                val parts = it.split("=", limit = 2)
                if (parts.size == 2) {
                    Pair(parts[0].trim(), parts[1].trim())
                } else null
            }
        }
    }

    var showAllCookieValues by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = host,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Site Information & Security",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Security Status Card
            item {
                val status = securityInfo?.status ?: if (url.startsWith("https://")) SecurityStatus.SECURE else SecurityStatus.NOT_SECURE
                val (statusBg, statusFg, statusTitle, statusDesc, statusIcon) = when (status) {
                    SecurityStatus.SECURE -> Tuple5(
                        Color(0xFFE8F5E9),
                        Color(0xFF2E7D32),
                        "Connection is secure",
                        "Your information (for example, passwords or credit card numbers) is private when it is sent to this site.",
                        Icons.Default.Lock
                    )
                    SecurityStatus.NOT_SECURE -> Tuple5(
                        Color(0xFFFFF3E0),
                        Color(0xFFE65100),
                        "Connection is not secure",
                        "You should not enter any sensitive information on this site (for example, passwords or credit cards), because it could be stolen by attackers.",
                        Icons.Default.LockOpen
                    )
                    SecurityStatus.CERTIFICATE_ERROR -> Tuple5(
                        Color(0xFFFFEBEE),
                        Color(0xFFC62828),
                        "Certificate Error",
                        securityInfo?.sslErrorDescription ?: "The security certificate for this website is invalid or untrusted.",
                        Icons.Default.Warning
                    )
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = statusBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = statusFg,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = statusTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = statusFg
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = statusDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = statusFg.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Section 2: Certificate Details (if HTTPS)
            if (url.startsWith("https://") && securityInfo != null) {
                item {
                    Text(
                        text = "CERTIFICATE DETAILS",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            securityInfo.issuedTo?.let { issuedTo ->
                                InfoRow("Issued To (CN)", issuedTo["CN"] ?: host)
                                issuedTo["O"]?.let { InfoRow("Organization (O)", it) }
                                issuedTo["OU"]?.let { InfoRow("Organizational Unit (OU)", it) }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            }

                            securityInfo.issuedBy?.let { issuedBy ->
                                InfoRow("Issued By (CN)", issuedBy["CN"] ?: "Unknown Issuer")
                                issuedBy["O"]?.let { InfoRow("Issuer Org (O)", it) }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            }

                            securityInfo.validFrom?.let { InfoRow("Valid From", it) }
                            securityInfo.validTo?.let { InfoRow("Valid Until", it) }
                            securityInfo.expiresInDays?.let { days ->
                                val expiryText = if (days >= 0) "Expires in $days days" else "Expired ${-days} days ago"
                                InfoRow("Expiry Status", expiryText)
                            }

                            securityInfo.serialNumber?.let {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                InfoRow("Serial Number", it)
                            }
                            securityInfo.sigAlgName?.let { InfoRow("Signature Algorithm", it) }
                            securityInfo.pubKeyInfo?.let { InfoRow("Public Key", it) }
                            securityInfo.san?.takeIf { it.isNotEmpty() }?.let {
                                InfoRow("Subject Alt Names", it.joinToString(", "))
                            }
                            securityInfo.sha256Fingerprint?.let {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                InfoRow("SHA-256 Fingerprint", it)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = {
                                    val fullDetails = buildString {
                                        appendLine("Site: $host")
                                        appendLine("Issued To: ${securityInfo.issuedTo}")
                                        appendLine("Issued By: ${securityInfo.issuedBy}")
                                        appendLine("Valid From: ${securityInfo.validFrom}")
                                        appendLine("Valid To: ${securityInfo.validTo}")
                                        appendLine("Expires In Days: ${securityInfo.expiresInDays}")
                                        appendLine("Serial Number: ${securityInfo.serialNumber}")
                                        appendLine("Signature Algorithm: ${securityInfo.sigAlgName}")
                                        appendLine("SHA-256 Fingerprint: ${securityInfo.sha256Fingerprint}")
                                    }
                                    copyToClipboard(context, "Certificate Details", fullDetails, false)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Copy All Certificate Details")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Section 3: Cookies
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "COOKIES (${cookiesList.size})",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (cookiesList.isNotEmpty()) {
                        IconButton(
                            onClick = { showAllCookieValues = !showAllCookieValues },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (showAllCookieValues) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showAllCookieValues) "Hide values" else "Show values",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (cookiesList.isEmpty()) {
                            Text(
                                text = "No cookies stored for this site.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            cookiesList.forEachIndexed { index, (name, value) ->
                                var showSingleValue by remember { mutableStateOf(false) }
                                val isVisible = showAllCookieValues || showSingleValue

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = name,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (isVisible) value else "••••••••",
                                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = if (isVisible) 3 else 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    IconButton(
                                        onClick = { showSingleValue = !showSingleValue },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            copyToClipboard(context, "Cookie", "$name=$value", isSensitive = true)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy cookie",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                if (index < cookiesList.size - 1) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = {
                                    val formatted = cookiesList.joinToString("; ") { "${it.first}=${it.second}" }
                                    copyToClipboard(context, "Cookies Header", formatted, isSensitive = true)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Copy All Cookies (Header Format)")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Note: Only cookies sent to this site are listed. Attributes (expiry, HttpOnly, Secure) are not exposed by Android.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Section 4: Site Settings Overrides
            item {
                Text(
                    text = "SITE SETTINGS (PER-SITE OVERRIDES)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))

                val curJs = siteSettings?.javascript ?: 0
                val curDesktop = siteSettings?.desktop ?: 0
                val curAdBlock = siteSettings?.adBlock ?: 0

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TriStateSettingRow(
                            title = "JavaScript",
                            value = curJs,
                            onValueChange = { newJs ->
                                onUpdateSiteSettings(newJs, curDesktop, curAdBlock)
                                Toast.makeText(context, "JavaScript setting updated for $host. Reloading...", Toast.LENGTH_SHORT).show()
                                onReloadPage()
                            }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        TriStateSettingRow(
                            title = "Desktop Site Mode",
                            value = curDesktop,
                            onValueChange = { newDesktop ->
                                onUpdateSiteSettings(curJs, newDesktop, curAdBlock)
                                Toast.makeText(context, "Desktop site mode updated for $host. Reloading...", Toast.LENGTH_SHORT).show()
                                onReloadPage()
                            }
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        TriStateSettingRow(
                            title = "Ad & Tracker Blocking",
                            value = curAdBlock,
                            onValueChange = { newAdBlock ->
                                onUpdateSiteSettings(curJs, curDesktop, newAdBlock)
                                Toast.makeText(context, "Ad & tracker block setting updated for $host. Reloading...", Toast.LENGTH_SHORT).show()
                                onReloadPage()
                            }
                        )

                        if (siteSettings != null && (curJs != 0 || curDesktop != 0 || curAdBlock != 0)) {
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedButton(
                                onClick = {
                                    onResetSiteSettings()
                                    Toast.makeText(context, "Site settings reset for $host. Reloading...", Toast.LENGTH_SHORT).show()
                                    onReloadPage()
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Reset Site Settings to Default")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TriStateSettingRow(
    title: String,
    value: Int, // 0: Default, 1: On, 2: Off
    onValueChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        SegmentedControl(
            tabs = listOf("Default", "On", "Off"),
            selectedIndex = value,
            onTabSelected = { onValueChange(it) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun copyToClipboard(context: Context, label: String, text: String, isSensitive: Boolean) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    if (isSensitive && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        clip.description.extras = android.os.PersistableBundle().apply {
            putBoolean(android.content.ClipDescription.EXTRA_IS_SENSITIVE, true)
        }
    }
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
}

private data class Tuple5<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)
