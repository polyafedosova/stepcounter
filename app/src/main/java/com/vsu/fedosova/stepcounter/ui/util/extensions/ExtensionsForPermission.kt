package com.vsu.fedosova.stepcounter.ui.util.extensions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.vsu.fedosova.stepcounter.R

fun AppCompatActivity.goToAppSetting() {
    val appSettingIntent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
    if (packageManager.resolveActivity(appSettingIntent,
            PackageManager.MATCH_DEFAULT_ONLY) == null
    ) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dper_no_permission))
            .setMessage(getString(R.string.dper_ban_forever))
            .create()
            .show()
    } else {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dper_no_permission))
            .setMessage(getString(R.string.dper_message))
            .setPositiveButton(getString(R.string.dper_allow)) { _, _ ->
                startActivity(appSettingIntent)
            }
            .create()
            .show()
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun AppCompatActivity.requestStepSensorPermission(requestCodePermission: Int) {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
        != PackageManager.PERMISSION_GRANTED
    ) {
        requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
            requestCodePermission)
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
fun AppCompatActivity.checkingStepSensorPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
}
