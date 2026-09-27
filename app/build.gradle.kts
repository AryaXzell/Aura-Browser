import java.util.Calendar
import java.util.TimeZone

fun generateVersionCode(): Int {
    // Jumlah menit sejak 1 Jan 2024 00:00 UTC ditambahkan offset 100,000,000.
    // Offset 100,000,000 memastikan versionCode baru (101M+) selalu lebih besar
    // dari skema terdahulu (yyMMddHH, misal ~26M di tahun 2026) sehingga update APK tidak ditolak.
    // Memberikan presisi per menit (mencegah bentrokan build di jam yang sama)
    // dan tetap jauh di bawah batas Play Store (2,100,000,000) hingga tahun ~2099 (~140M).
    val epochMillis = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(2024, Calendar.JANUARY, 1, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val nowMillis = System.currentTimeMillis()
    val minutesSinceEpoch = ((nowMillis - epochMillis) / 60000L).toInt()
    val baseOffset = 100_000_000
    return baseOffset + minutesSinceEpoch
}

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
}

android {
  namespace = "com.aryaxzell.aurabrowser"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aryaxzell.aurabrowser"
    minSdk = 24
    targetSdk = 36
    versionCode = generateVersionCode()
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  splits {
    abi {
      isEnable = project.hasProperty("splitApks")
      reset()
      include("arm64-v8a", "armeabi-v7a")
      isUniversalApk = true
    }
  }

  signingConfigs {
    create("release") {
      val keystorePathEnv = System.getenv("KEYSTORE_PATH")
      val defaultKeystoreFile = file("${rootDir}/aura-browser-release.jks")
      val resolvedKeystoreFile = if (!keystorePathEnv.isNullOrBlank()) {
          file(keystorePathEnv)
      } else {
          defaultKeystoreFile
      }

      val isReleaseBuild = gradle.startParameter.taskNames.any { task ->
          task.contains("Release", ignoreCase = true) || task == "assemble" || task == "build"
      }

      if (isReleaseBuild) {
          if (!resolvedKeystoreFile.exists()) {
              throw org.gradle.api.GradleException(
                  "Release keystore tidak ditemukan di '${resolvedKeystoreFile.absolutePath}'. " +
                  "JANGAN generate keystore baru secara otomatis untuk build release — ini akan " +
                  "menyebabkan signature mismatch dengan versi yang sudah terinstall di device manapun. " +
                  "Salin keystore release yang SAMA (dibuat sekali, disimpan persisten) ke path ini, " +
                  "atau set environment variable KEYSTORE_PATH menunjuk ke lokasinya sebelum build dijalankan."
              )
          }

          storeFile = resolvedKeystoreFile
          storePassword = System.getenv("STORE_PASSWORD")
              ?: throw org.gradle.api.GradleException("Environment variable STORE_PASSWORD belum di-set untuk build release.")
          keyAlias = "upload"
          keyPassword = System.getenv("KEY_PASSWORD")
              ?: throw org.gradle.api.GradleException("Environment variable KEY_PASSWORD belum di-set untuk build release.")
      } else {
          storeFile = resolvedKeystoreFile
          storePassword = System.getenv("STORE_PASSWORD") ?: "dummy_password"
          keyAlias = "upload"
          keyPassword = System.getenv("KEY_PASSWORD") ?: "dummy_password"
      }
    }
  }

  buildTypes {
    release {
      isCrunchPngs = true
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
      all {
        it.systemProperty("robolectric.sdk", "35")
      }
    }
  }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)

  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
}
