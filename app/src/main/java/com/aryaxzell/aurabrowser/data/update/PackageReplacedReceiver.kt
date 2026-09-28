package com.aryaxzell.aurabrowser.data.update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PackageReplacedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            // Bersihkan seluruh paket instalasi (APK, zip artefak, file temporary) setelah app berhasil diinstal/diupdate
            AppUpdateManager.cleanUpdateArtifacts(context)
        }
    }
}
