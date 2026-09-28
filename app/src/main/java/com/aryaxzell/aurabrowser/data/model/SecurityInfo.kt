package com.aryaxzell.aurabrowser.data.model

enum class SecurityStatus {
    SECURE,
    NOT_SECURE,
    CERTIFICATE_ERROR
}

data class SecurityInfo(
    val url: String,
    val status: SecurityStatus,
    val sslErrorDescription: String? = null,
    val issuedTo: Map<String, String>? = null,
    val issuedBy: Map<String, String>? = null,
    val validFrom: String? = null,
    val validTo: String? = null,
    val expiresInDays: Long? = null,
    val serialNumber: String? = null,
    val sigAlgName: String? = null,
    val pubKeyInfo: String? = null,
    val san: List<String>? = null,
    val version: Int? = null,
    val sha256Fingerprint: String? = null
)
