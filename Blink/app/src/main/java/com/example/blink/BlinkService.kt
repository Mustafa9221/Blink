package com.example.blink

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat

class BlinkService : Service() {

    private lateinit var screenReceiver: ScreenStateReceiver
    private val CHANNEL_ID = "BlinkServiceChannel"

    private val handler = Handler(Looper.getMainLooper())

    // ⏱ Runs every second
    private val ticker = object : Runnable {
        override fun run() {

            // Check while screen is ON
            val r2 = ScreenUsageController.checkWhileOn()
            if (r2 == ScreenUsageController.Result.ENFORCE_LOCK) {
                Log.d("Blink", "Enforcing lock (usage limit reached)")
                OverlayManager.showAlert(
                    this@BlinkService,
                    "👀 Protect your eyes\n\nLook away for a moment"
                )
                ScreenLocker.lock(this@BlinkService)
            }

            // Check while screen is OFF (during penalty)
            val r3 = ScreenUsageController.checkWhileOff()
            if (r3 == ScreenUsageController.Result.BREAK_COMPLETED) {
                Log.d("Blink", "Break completed")
                OverlayManager.hideAlert(this@BlinkService)
                Notifier.ringPhone(context = this@BlinkService);
            }

            handler.postDelayed(this, 1000) // ⏱ every 1 sec
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("Blink", "BlinkService created")

        createNotificationChannel()

        // 📡 Register screen ON/OFF receiver
        screenReceiver = ScreenStateReceiver()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(screenReceiver, filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("Blink", "BlinkService started")
        val notification: Notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Blink is running")
                .setContentText("Protecting your eyes")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true)
                .build()

        startForeground(1, notification)

        // ▶️ Start per-second checks
        handler.post(ticker)

        return START_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenReceiver)
        handler.removeCallbacks(ticker)

        OverlayManager.hideAlert(this)

        Log.d("Blink", "BlinkService destroyed")
    }


    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Blink Foreground Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }


}
