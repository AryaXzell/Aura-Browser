package com.aryaxzell.aurabrowser.data.translation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object GeminiTranslationService {

    /**
     * Translates the given text to the target language using Google Gemini API.
     */
    suspend fun translateText(
        text: String,
        targetLanguage: String,
        apiKey: String,
        modelName: String = "gemini-2.5-flash"
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(Exception("Gemini API Key is empty. Please enter your API key in Settings."))
            }

            val sanitizedModel = modelName.removePrefix("models/")
            val urlString = "https://generativelanguage.googleapis.com/v1beta/models/$sanitizedModel:generateContent?key=$apiKey"
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            val prompt = "Translate the following text to $targetLanguage. Retain paragraphs and structure. Return ONLY the translation without any conversational filler or introductions:\n\n$text"

            // Construct raw JSON payload to avoid heavy dependency overhead
            val payload = JSONObject().apply {
                put("contents", org.json.JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", org.json.JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
            }

            conn.outputStream.use { os ->
                os.write(payload.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val jsonResponse = JSONObject(response.toString())
                val candidates = jsonResponse.optJSONArray("candidates")
                val parts = candidates?.optJSONObject(0)
                    ?.optJSONObject("content")
                    ?.optJSONArray("parts")
                val translatedText = parts?.optJSONObject(0)?.optString("text")

                if (!translatedText.isNullOrBlank()) {
                    Result.success(translatedText)
                } else {
                    Result.failure(Exception("Failed to parse translation from Gemini response."))
                }
            } else {
                val errorReader = BufferedReader(InputStreamReader(conn.errorStream ?: conn.inputStream, "UTF-8"))
                val errorResponse = StringBuilder()
                var line: String?
                while (errorReader.readLine().also { line = it } != null) {
                    errorResponse.append(line)
                }
                errorReader.close()
                Result.failure(Exception("Gemini API error ($responseCode): ${errorResponse.take(150)}..."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Dynamically fetches the list of available models from Google servers.
     */
    suspend fun fetchAvailableModels(apiKey: String): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            if (apiKey.isBlank()) {
                return@withContext Result.failure(Exception("API Key is blank"))
            }

            val urlString = "https://generativelanguage.googleapis.com/v1beta/models?key=$apiKey"
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Content-Type", "application/json")

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val jsonResponse = JSONObject(response.toString())
                val modelsArray = jsonResponse.optJSONArray("models") ?: org.json.JSONArray()
                val modelsList = mutableListOf<String>()
                for (i in 0 until modelsArray.length()) {
                    val mObj = modelsArray.optJSONObject(i)
                    val name = mObj?.optString("name") ?: ""
                    // Only include generateContent models
                    val supportedActions = mObj?.optJSONArray("supportedGenerationMethods")
                    var supportsGenerate = false
                    if (supportedActions != null) {
                        for (j in 0 until supportedActions.length()) {
                            if (supportedActions.optString(j) == "generateContent") {
                                supportsGenerate = true
                                break
                            }
                        }
                    }
                    if (name.startsWith("models/") && supportsGenerate) {
                        modelsList.add(name.removePrefix("models/"))
                    }
                }
                Result.success(modelsList.sorted())
            } else {
                Result.failure(Exception("Server returned response code $responseCode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
