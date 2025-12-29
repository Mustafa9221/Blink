package com.example.blink

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.util.Log

object ScreenLocker {

    fun lock(context: Context) {
        val dpm =
            context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

        val adminComponent = ComponentName(context, MyDeviceAdminReceiver::class.java)

        if (dpm.isAdminActive(adminComponent)) {
            Log.d("Blink", "Locking screen now")
            dpm.lockNow()
        } else {
            Log.e("Blink", "Device admin not active. Cannot lock screen.")
        }
    }
}
