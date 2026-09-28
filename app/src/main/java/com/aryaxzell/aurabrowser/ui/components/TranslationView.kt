package com.aryaxzell.aurabrowser.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.aryaxzell.aurabrowser.data.localization.AppStrings
import com.aryaxzell.aurabrowser.data.model.AppLanguage
import com.aryaxzell.aurabrowser.data.translation.GeminiTranslationService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationView(
    currentLanguage: AppLanguage,
    currentUrl: String,
    translatorEngine: String, // "google" or "gemini"
    geminiApiKey: String,
    geminiModel: String,
    onUpdateTranslatorEngine: (String) -> Unit,
    onUpdateGeminiApiKey: (String) -> Unit,
    onUpdateGeminiModel: (String) -> Unit,
    onGoogleTranslateUrl: (String) -> Unit, // load the Google translate URL in webview
    onTranslateWithGemini: (String, (String) -> Unit) -> Unit, // callback to run JS script and pass text to callback
    onDismiss: () -> Unit
) {
    val strings = remember(currentLanguage) { AppStrings(currentLanguage) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // Supported languages
    val translationLanguages = listOf(
        "id" to "Bahasa Indonesia",
        "en" to "English",
        "ru" to "Русский",
        "es" to "Español",
        "fr" to "Français",
        "ja" to "日本語",
        "zh" to "中文"
    )

    var targetLangCode by remember {
        mutableStateOf(
            when (currentLanguage) {
                AppLanguage.ID -> "id"
                AppLanguage.RU -> "ru"
                AppLanguage.EN -> "en"
            }
        )
    }

    var showEngineMenu by remember { mutableStateOf(false) }
    var showModelMenu by remember { mutableStateOf(false) }
    var customApiKey by remember { mutableStateOf(geminiApiKey) }

    // Dynamic model check states
    var checkingModels by remember { mutableStateOf(false) }
    var availableModels by remember { mutableStateOf<List<String>>(emptyList()) }
    var checkError by remember { mutableStateOf<String?>(null) }

    // Translation states
    var isTranslating by remember { mutableStateOf(false) }
    var translationError by remember { mutableStateOf<String?>(null) }
    var translatedResultText by remember { mutableStateOf<String?>(null) }

    // Save key helper
    val saveApiKey = {
        onUpdateGeminiApiKey(customApiKey)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
                .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = strings.translate,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = strings.close,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Translator Engine Selector
            TranslationCardGroup(
                header = strings.selectTranslatorEngine
            ) {
                TranslationSettingsRow(
                    title = "Engine",
                    subtitle = if (translatorEngine == "google") "Google Translate" else "Google Gemini AI",
                    trailingContent = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    onClick = { showEngineMenu = true }
                )
            }

            // Engine Dropdown
            if (showEngineMenu) {
                AlertDialog(
                    onDismissRequest = { showEngineMenu = false },
                    title = { Text(strings.selectTranslatorEngine) },
                    text = {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onUpdateTranslatorEngine("google")
                                        showEngineMenu = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = translatorEngine == "google", onClick = {
                                    onUpdateTranslatorEngine("google")
                                    showEngineMenu = false
                                })
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Google Translate (Sangat Cepat & Seamless)")
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onUpdateTranslatorEngine("gemini")
                                        showEngineMenu = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = translatorEngine == "gemini", onClick = {
                                    onUpdateTranslatorEngine("gemini")
                                    showEngineMenu = false
                                })
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Google Gemini AI (Premium Reader Mode)")
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showEngineMenu = false }) {
                            Text(strings.cancel)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gemini API Key & Model Config Area (only if Gemini AI is selected)
            if (translatorEngine == "gemini") {
                TranslationCardGroup(
                    header = "GEMINI CONFIGURATION"
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Text(
                            text = strings.geminiApiKeyLabel,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = customApiKey,
                            onValueChange = {
                                customApiKey = it
                                onUpdateGeminiApiKey(it)
                            },
                            placeholder = { Text(strings.geminiApiKeyPlaceholder, fontSize = 13.sp) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                saveApiKey()
                                focusManager.clearFocus()
                            }),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Model Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = strings.geminiModelLabel,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = geminiModel,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            TextButton(onClick = { showModelMenu = true }) {
                                Text("Ubah Model", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        // Model check button
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                checkingModels = true
                                checkError = null
                                coroutineScope.launch {
                                    val result = GeminiTranslationService.fetchAvailableModels(customApiKey)
                                    checkingModels = false
                                    if (result.isSuccess) {
                                        availableModels = result.getOrNull() ?: emptyList()
                                        if (availableModels.isEmpty()) {
                                            checkError = "No supported models found. Check API key."
                                        }
                                    } else {
                                        checkError = result.exceptionOrNull()?.message ?: "Gagal mengambil daftar model."
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                        ) {
                            if (checkingModels) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(strings.checkModels)
                        }

                        // Checking models list results
                        if (availableModels.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "${strings.modelsAvailable}:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    availableModels.take(8).forEach { model ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    onUpdateGeminiModel(model)
                                                }
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(model, style = TextStyle(fontFamily = FontFamily.Monospace), fontSize = 11.sp)
                                            if (geminiModel == model) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        checkError?.let { err ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(err, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }

                // Model Selection dialog
                if (showModelMenu) {
                    val defaultModels = listOf("gemini-2.5-flash", "gemini-3.5-flash", "gemini-3.1-pro-preview")
                    AlertDialog(
                        onDismissRequest = { showModelMenu = false },
                        title = { Text(strings.geminiModelLabel) },
                        text = {
                            Column {
                                defaultModels.forEach { m ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onUpdateGeminiModel(m)
                                                showModelMenu = false
                                            }
                                            .padding(vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(selected = geminiModel == m, onClick = {
                                            onUpdateGeminiModel(m)
                                            showModelMenu = false
                                        })
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(m)
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showModelMenu = false }) {
                                Text(strings.cancel)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Target Language Selector Card
            TranslationCardGroup(
                header = strings.translateTo
            ) {
                translationLanguages.forEachIndexed { idx, pair ->
                    val isSelected = targetLangCode == pair.first
                    TranslationSettingsRow(
                        title = pair.second,
                        trailingContent = {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        onClick = { targetLangCode = pair.first }
                    )
                    if (idx < translationLanguages.size - 1) {
                        TranslationHairlineDivider()
                    }
                }
            }

            translationError?.let { err ->
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(err, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Big Action Button
            Button(
                onClick = {
                    if (translatorEngine == "google") {
                        // Google Translate wrapping: simple and incredibly seamless
                        val wrappedUrl = "https://translate.google.com/translate?sl=auto&tl=$targetLangCode&u=${java.net.URLEncoder.encode(currentUrl, "UTF-8")}"
                        onGoogleTranslateUrl(wrappedUrl)
                        coroutineScope.launch {
                            sheetState.hide()
                            onDismiss()
                        }
                    } else {
                        // Gemini AI Translation: Reader Mode flow
                        if (customApiKey.isBlank()) {
                            translationError = "Gemini API Key is required. Please fill it above."
                            return@Button
                        }
                        translationError = null
                        isTranslating = true
                        onTranslateWithGemini(targetLangCode) { extractedText ->
                            if (extractedText.isBlank()) {
                                isTranslating = false
                                translationError = "Failed to extract text from the website. Make sure page is loaded."
                                return@onTranslateWithGemini
                            }
                            coroutineScope.launch {
                                val result = GeminiTranslationService.translateText(
                                    text = extractedText,
                                    targetLanguage = translationLanguages.firstOrNull { it.first == targetLangCode }?.second ?: "English",
                                    apiKey = customApiKey,
                                    modelName = geminiModel
                                )
                                isTranslating = false
                                if (result.isSuccess) {
                                    translatedResultText = result.getOrNull()
                                } else {
                                    translationError = result.exceptionOrNull()?.message ?: "Unknown error calling Gemini."
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                enabled = !isTranslating
            ) {
                if (isTranslating) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.5.dp, color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.translating)
                } else {
                    Text(strings.translate)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // AI Translation Reader Mode Modal overlay
    translatedResultText?.let { resultText ->
        AIReaderModeDialog(
            title = translationLanguages.firstOrNull { it.first == targetLangCode }?.second ?: "Translated",
            originalUrl = currentUrl,
            translatedText = resultText,
            onDismiss = {
                translatedResultText = null
            }
        )
    }
}

/**
 * A highly polished, fully screen-filling Reader Mode viewer that displays Gemini translated website text with outstanding typography, mirroring iOS Safari's translation UI.
 */
@Composable
fun AIReaderModeDialog(
    title: String,
    originalUrl: String,
    translatedText: String,
    onDismiss: () -> Unit
) {
    var textScale by remember { mutableFloatStateOf(1f) }

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
                // Reader Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = {
                            if (textScale > 0.8f) textScale -= 0.1f
                        }) {
                            Text("A-", style = MaterialTheme.typography.bodyLarge)
                        }
                        TextButton(onClick = {
                            if (textScale < 1.5f) textScale += 0.1f
                        }) {
                            Text("A+", style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    Text(
                        text = "AI Translated: $title",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                // Scrollable content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "Aura Reader Mode",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = originalUrl.removePrefix("https://").removePrefix("http://").substringBefore("/"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Main Translated Body
                    Text(
                        text = translatedText,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = (16 * textScale).sp,
                            lineHeight = (24 * textScale).sp,
                            fontFamily = FontFamily.Serif
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

// -------------------------------------------------------------
// REUSABLE iOS-STYLE COMPONENTS (LOCALIZED FOR TRANSLATION SHEET)
// -------------------------------------------------------------

@Composable
fun TranslationCardGroup(
    header: String? = null,
    footer: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (!header.isNullOrBlank()) {
            Text(
                text = header,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.5.sp,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 14.dp, bottom = 6.dp)
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
            border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content
            )
        }

        if (!footer.isNullOrBlank()) {
            Text(
                text = footer,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 14.dp, top = 6.dp, end = 14.dp)
            )
        }
    }
}

@Composable
fun TranslationSettingsRow(
    title: String,
    subtitle: String? = null,
    trailingContent: @Composable () -> Unit = {},
    onClick: (() -> Unit)? = null
) {
    val rowModifier = if (onClick != null) {
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    } else {
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp)
    }

    Row(
        modifier = rowModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        trailingContent()
    }
}

@Composable
fun TranslationHairlineDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 14.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
    )
}
