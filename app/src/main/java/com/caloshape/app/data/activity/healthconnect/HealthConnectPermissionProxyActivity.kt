package com.caloshape.app.data.activity.healthconnect

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.health.connect.client.PermissionController


class HealthConnectPermissionProxyActivity : ComponentActivity() {

    private var requiredPerms: Set<String> = emptySet()

    private val requestPerms =
        registerForActivityResult(PermissionController.createRequestPermissionResultContract()) { granted ->
            Log.d(TAG, "HC permission result: granted=${granted.size} $granted")

            val allGranted = requiredPerms.isNotEmpty() && granted.containsAll(requiredPerms)
            if (allGranted) {
                HealthConnectPermissionPrefs.resetDeniedCount(this)
            } else {

                HealthConnectPermissionPrefs.incrementDeniedCount(this)
            }

            finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val perms = intent.getStringArrayListExtra(EXTRA_PERMS)
        if (perms.isNullOrEmpty()) {
            finish()
            return
        }

        requiredPerms = perms.toSet()
        requestPerms.launch(requiredPerms)
    }

    companion object {
        private const val TAG = "HC_PROXY"
        private const val EXTRA_PERMS = "extra_hc_perms"

        fun start(ctx: Context, permissions: Set<String>) {
            val i = Intent(ctx, HealthConnectPermissionProxyActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                putStringArrayListExtra(EXTRA_PERMS, ArrayList(permissions))
            }
            ctx.startActivity(i)
        }
    }
}
