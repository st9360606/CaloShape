package com.caloshape.app

import android.app.Application
import androidx.work.Configuration
import androidx.hilt.work.HiltWorkerFactory
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.caloshape.app.data.fasting.notifications.FastingReceiver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import javax.inject.Named
import android.util.Log
import okhttp3.OkHttpClient

@HiltAndroidApp
class CaloShape : Application(), Configuration.Provider, ImageLoaderFactory {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject @Named("imageClient") lateinit var imageClient: OkHttpClient

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .okHttpClient(imageClient)
            .respectCacheHeaders(true)
            .build()

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
