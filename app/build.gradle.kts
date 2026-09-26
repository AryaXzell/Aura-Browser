import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.util.Locale

fun generateVersionCode(): Int {
    // Format: YYMMDDHH sebagai Int — menjamin nilai yang selalu naik seiring waktu,
    // sekaligus tetap dalam batas aman Int (maksimum versionCode yang diizinkan Play Store
    // adalah 2100000000 — format YYMMDDHH menghasilkan nilai jauh di bawah itu hingga tahun 2099).
    val formatter = SimpleDateFormat("yyMMddHH", Locale.US)
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date()).toInt()
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
      isCrunchPngs = false
      isMinifyEnabled = false
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
