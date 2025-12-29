package com.example.blink

import android.widget.Button
import android.app.Activity
import android.content.Intent
import android.util.Log

class ButtonFunctionalities {
    fun setupGrantAdminButton(activity: Activity, permissions: Permissions) {
        val btnGrantAdmin =
            activity.findViewById<Button>(R.id.btnGrantAdmin)

        btnGrantAdmin.setOnClickListener {
            permissions.requestAdminPermission(activity)
        }
    }

    fun setupGrantForegroundButton(activity: Activity, permissions: Permissions){
        val btnGrandForegound = activity.findViewById<Button>(R.id.btnGrantForegroundService)
        btnGrandForegound.setOnClickListener {
            permissions.requestForegroundServicePermission(activity)
        }
    }

    fun setupGrandOverlayButton(activity: Activity,permissions: Permissions){
        val btnOverly = activity.findViewById<Button>(R.id.btnGrantOverlay);

        btnOverly.setOnClickListener {
            permissions.requestOverlayPermission(activity)
        }
    }


    //Start Button Functionality Here
    fun setupStartBlinkButton(activity: Activity){
        val activityRef = MainActivity();
        val usageController = ScreenUsageController;
        val blinkService = BlinkService();
        val btnStart = activity.findViewById<Button>(R.id.btnStartBlink)
        btnStart.setOnClickListener {
            usageController.onScreenOn()
            btnStart.text="Stop"
            activityRef.stopBlink()
        }
    }

}