package com.caloshape.app.data.fasting.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object NotificationPermission {

    
    fun isGranted(context: Context): Boolean {

        val appEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        if (!appEnabled) return false

        // 2) Android 13+ runtime permission
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
