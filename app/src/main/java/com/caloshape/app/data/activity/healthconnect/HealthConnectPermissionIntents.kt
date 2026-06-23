package com.caloshape.app.data.activity.healthconnect

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.health.connect.client.HealthConnectClient

/**
 * ?—è©¦?“é? Health Connect æ¬Šé?/è¨­å??ï?
 * - Android 14+ï¼šå„ª?ˆæ??‹ã€ŒManage health permissions???¯å¸¶ EXTRA_PACKAGE_NAME ?‡å???app)
 * - ?¶ä?ï¼šæ???Health Connect è¨­å???
 * - ?¥éƒ½å¤±æ?ï¼šfallback ??Play Storeï¼ˆAndroid 13??å¸¸è?ï¼‰æ? App details
 */
object HealthConnectPermissionIntents {

    private const val TAG = "HC_INTENTS"

    // Android 14+ ?„å¥åº·æ??ç®¡?†é? actionï¼ˆAPI 34+ï¼?
    private const val ACTION_MANAGE_HEALTH_PERMISSIONS =
        "android.health.connect.action.MANAGE_HEALTH_PERMISSIONS"

    /**
     * @return true è¡¨ç¤º?å? startActivityï¼›false è¡¨ç¤º?€?‰æ–¹æ¡ˆéƒ½?¡æ??“é?
     */
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

        // ?ˆç? SDK ?¯å¦?¯ç”¨ï¼ˆAndroid 13??æ²’è? HC ?‚æ?ä¸æ˜¯ AVAILABLEï¼?
        val sdkStatus = HealthConnectClient.getSdkStatus(ctx)
        Log.d(TAG, "sdkStatus=$sdkStatus sdkInt=${Build.VERSION.SDK_INT}")

        // Android 14+ï¼šå??—è©¦?´é???app ?„å¥åº·æ??ç®¡?†é?ï¼ˆè‹¥è£ç½®?¯æ´ï¼?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val manage = Intent(ACTION_MANAGE_HEALTH_PERMISSIONS).apply {
                // å®˜æ–¹/å¹³å°?‡ä»¶ï¼šå¯?¸å¸¶ Intent.EXTRA_PACKAGE_NAME ?‡å??¹å? app :contentReference[oaicite:3]{index=3}
                putExtra(Intent.EXTRA_PACKAGE_NAME, ctx.packageName)
            }
            if (tryStart(manage)) return true
        }

        // fallbackï¼šæ???Health Connect è¨­å??ï??€ HC ?¯ç”¨ï¼?
        val hcSettings = Intent(HealthConnectClient.ACTION_HEALTH_CONNECT_SETTINGS)
        if (tryStart(hcSettings)) return true

        // ??fallbackï¼šAndroid 13??å¸¸è??¯æ?è£?Health Connectï¼Œå? Play Store
        // å®˜æ–¹?‡ä»¶ï¼šAndroid 13???€è¦å?è£?Health Connect app :contentReference[oaicite:4]{index=4}
        val playStore = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.healthdata"))
        if (tryStart(playStore)) return true

        val playWeb = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata"))
        if (tryStart(playWeb)) return true

        // ?€å¾?fallbackï¼šè‡³å°‘æ??‹ä? app è©³ç´°è¨­å??ï?è®“ä½¿?¨è€…æ??°æ–¹?¯æ?ä½œï?
        val appDetails = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", ctx.packageName, null)
        }
        return tryStart(appDetails)
    }
}
