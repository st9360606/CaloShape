package com.caloshape.app

import android.app.Application
import androidx.work.Configuration
import androidx.hilt.work.HiltWorkerFactory
import com.caloshape.app.data.fasting.notifications.FastingReceiver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import android.util.Log

@HiltAndroidApp
class CaloShape : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    // ??WorkManager 2.9.x ä»¥å±¬?§è?å¯?
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.INFO) // ?¯èª¿??VERBOSE ä»¥é™¤??
            .build()

    override fun onCreate() {
        super.onCreate()
        // ?å?å»ºç??šçŸ¥?»é?ï¼ˆé¿?ç¬¬ä¸€æ¬¡ç™¼?šçŸ¥?æ??‰é »?“ï?
        // ???ªè??™è??‰å‡º?¾åœ¨ Logcatï¼Œå°±ä»?¡¨ app ?³å??‰æ??Ÿå??•åˆ° Application
        Log.d("CalAiApp", "Application onCreate() started")
        FastingReceiver.ensureChannel(this)
    }
}
