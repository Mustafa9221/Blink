package com.example.blink

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView


object OverlayManager {

    private var overlayView: View? = null

    fun showAlert(context: Context, message: String, durationMs: Long = 0L) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            !Settings.canDrawOverlays(context)
        ) return

        if (overlayView != null) return

        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val view = LayoutInflater.from(context)
            .inflate(R.layout.layout_eye_alert, null)

        view.findViewById<TextView>(R.id.tvAlertMessage).text = message

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        windowManager.addView(view, params)
        overlayView = view

        // ⏱ Auto-hide if duration > 0
        if (durationMs > 0) {
            Handler(Looper.getMainLooper()).postDelayed({
                hideAlert(context)
            }, durationMs)
        }
    }

    fun hideAlert(context: Context) {
        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }
}
