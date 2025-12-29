package com.example.blink

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import android.Manifest
import android.net.Uri
import android.provider.Settings


class Permissions {
    fun isAdminEnabled(context: Context): Boolean {
        val dpm =
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        val adminComponent =
            ComponentName(context, MyDeviceAdminReceiver::class.java)

        return dpm.isAdminActive(adminComponent)
    }

    fun requestAdminPermission(activity: Activity) {
        val adminComponent =
            ComponentName(activity, MyDeviceAdminReceiver::class.java)

        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Blink needs this permission to lock your screen during focus sessions."
            )
        }

        activity.startActivity(intent)
    }


    companion object {
        const val REQ_NOTIF_PERMISSION = 1001
    }

    // ✅ True if we should SKIP asking (Android < 13)
    fun canSkipForegroundPermission(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
    }

    // ✅ Check if notification permission is granted (Android 13+)
    fun isForegroundServiceAllowed(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Below 13 → always allowed
        }
    }

    fun requestForegroundServicePermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                // User denied before → show dialog again
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permission),
                    REQ_NOTIF_PERMISSION
                )
            } else {
                // Either first time OR "Don't ask again"
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permission),
                    REQ_NOTIF_PERMISSION
                )
            }
        }
    }

    fun isOverlayAllowed(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }
    fun requestOverlayPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${activity.packageName}")
            )
            activity.startActivity(intent)
        }
    }
}
