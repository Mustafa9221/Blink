package com.example.blink

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Build


class MainActivity : AppCompatActivity() {
    val buttonsFunctionality = ButtonFunctionalities();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionsChecker(this)
    }

    override fun onResume() {
        super.onResume()
        permissionsChecker(this)
    }


    //Code for Checking Admin Permission
    val permissions = Permissions();
    fun permissionsChecker(context: Context){
        if(!permissions.isAdminEnabled(context)){
            setContentView(R.layout.activity_admin_permission)
            buttonsFunctionality.setupGrantAdminButton(activity = this,permissions = permissions);
        }
        else if (!permissions.isForegroundServiceAllowed(this)){
            setContentView(R.layout.activity_bg_permission)
            buttonsFunctionality.setupGrantForegroundButton(activity = this,permissions = permissions)
        } else if(!permissions.isOverlayAllowed(this)){
            setContentView(R.layout.activity_overlay_permission)
            buttonsFunctionality.setupGrandOverlayButton(activity = this,permissions= permissions);
        }
        else{
            setContentView(R.layout.activity_main)
            startBlinkService();
            buttonsFunctionality.setupStartBlinkButton(activity = this)
        }
    }



    //ALl Main Foreground Service For Blink

    fun startBlinkService() {
        val intent = Intent(this, BlinkService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    fun stopBlink() {
        moveTaskToBack(true)
    }


}
