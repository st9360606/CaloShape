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

    // ??WorkManager 2.9.x 以屬?��?�?
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.INFO) // ?�調??VERBOSE 以除??
            .build()

    override fun onCreate() {
        super.onCreate()
        Log.d("CalAiApp", "Application onCreate() started")
        FastingReceiver.ensureChannel(this)
    }
}
