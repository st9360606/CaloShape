package com.caloshape.app.data.activity.healthconnect

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.health.connect.client.HealthConnectClient


object HealthConnectPermissionIntents {

    private const val TAG = "HC_INTENTS"


    private const val ACTION_MANAGE_HEALTH_PERMISSIONS =
        "android.health.connect.action.MANAGE_HEALTH_PERMISSIONS"

    
    fun openHealthPermissionsSettings(ctx: Context): Boolean {
        val pm = ctx.packageManager

        fun tryStart(i: Intent): Boolean {
            val intent = i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val resolved = intent.resolveActivity(pm) != null
            Log.d(TAG, "tryStart resolved=$resolved intent=$intent")
            return if (resolved) {
                runCatching {
                    ctx.startActivity(intent)
                    true
                }.getOrElse { e ->
                    Log.e(TAG, "startActivity failed: ${e.javaClass.simpleName}: ${e.message}", e)
                    false
                }
            } else {
                false
            }
        }


        val sdkStatus = HealthConnectClient.getSdkStatus(ctx)
        Log.d(TAG, "sdkStatus=$sdkStatus sdkInt=${Build.VERSION.SDK_INT}")


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val manage = Intent(ACTION_MANAGE_HEALTH_PERMISSIONS).apply {

                putExtra(Intent.EXTRA_PACKAGE_NAME, ctx.packageName)
            }
            if (tryStart(manage)) return true
        }


        val hcSettings = Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS)
        if (tryStart(hcSettings)) return true



        val playStore = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.healthdata"))
        if (tryStart(playStore)) return true

        val playWeb = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata"))
        if (tryStart(playWeb)) return true


        val appDetails = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", ctx.packageName, null)
        }
        return tryStart(appDetails)
    }
}
