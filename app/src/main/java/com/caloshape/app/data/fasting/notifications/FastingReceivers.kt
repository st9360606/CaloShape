package com.caloshape.app.data.fasting.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.caloshape.app.R
import com.caloshape.app.data.fasting.notifications.FastingAlarmScheduler.Companion.ACTION_FASTING_END_SOON
import com.caloshape.app.data.fasting.notifications.FastingAlarmScheduler.Companion.ACTION_FASTING_START
import com.caloshape.app.data.fasting.notifications.FastingAlarmScheduler.Companion.EXTRA_END_TIME
import com.caloshape.app.data.fasting.notifications.FastingAlarmScheduler.Companion.EXTRA_PLAN_CODE
import com.caloshape.app.data.fasting.notifications.FastingAlarmScheduler.Companion.EXTRA_START_TIME

class FastingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // ???•ÈÄöÁü•Á∏ΩÈ???Ê¨äÈ?Ë¢´È?ÔºöÁ??ªÊú¨?∞Êî∂?ÇÔ??øÂ?ÊØèÂ§©Ë¢?alarm ?öÈ?
        if (!NotificationPermission.isGranted(context)) {
            FastingAlarmScheduler(context).cancel()
            return
        }

        ensureChannel(context)

        val planCode = intent.getStringExtra(EXTRA_PLAN_CODE).orEmpty()
        val startTime = intent.getStringExtra(EXTRA_START_TIME).orEmpty()
        val endTime = intent.getStringExtra(EXTRA_END_TIME).orEmpty()

        val nm = context.getSystemService(NotificationManager::class.java)

        val (id, title, text, shouldReschedule) = when (intent.action) {

            ACTION_FASTING_START -> {
                val tOv = FastingNotificationTemplates.getStartTitleOverride(context)
                val bOv = FastingNotificationTemplates.getStartBodyOverride(context)

                val title = if (!tOv.isNullOrBlank())
                    FastingNotificationTemplates.render(tOv, planCode, startTime, endTime)
                else
                    context.getString(R.string.fasting_start_title, planCode)

                val body = if (!bOv.isNullOrBlank())
                    FastingNotificationTemplates.render(bOv, planCode, startTime, endTime)
                else
                    context.getString(R.string.fasting_start_body, startTime, endTime)

                Quad(2001, title, body, false)
            }

            ACTION_FASTING_END_SOON -> {
                val tOv = FastingNotificationTemplates.getEndSoonTitleOverride(context)
                val bOv = FastingNotificationTemplates.getEndSoonBodyOverride(context)

                val title = if (!tOv.isNullOrBlank())
                    FastingNotificationTemplates.render(tOv, planCode, startTime, endTime)
                else
                    context.getString(R.string.fasting_endsoon_title)

                val body = if (!bOv.isNullOrBlank())
                    FastingNotificationTemplates.render(bOv, planCode, startTime, endTime)
                else
                    context.getString(R.string.fasting_endsoon_body, endTime)

                // ??endSoon ?ÅÂá∫ÂæåÔ??í‰?‰∏ÄËº™Ô??éÂ§©???ÁµÑÔ?
                Quad(2002, title, body, true)
            }

            else -> return
        }

        val n = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.notifications)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .build()

        nm.notify(id, n)

        // ???ªÈ? enqueueÔºöÈÅø??endSoon Ë¢´Ëß∏?ºÂ?Ê¨°Ê???worker
        if (shouldReschedule) enqueueRescheduleUnique(context)
    }

    private fun enqueueRescheduleUnique(context: Context) {
        val req = OneTimeWorkRequestBuilder<RescheduleWorker>().build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            req
        )
    }

    companion object {
        const val CHANNEL_ID = "fasting_plan"
        private const val UNIQUE_WORK_NAME = "fasting_reschedule"

        fun ensureChannel(context: Context) {
            // ??Android 8.0+ ?çÈ?Ë¶?NotificationChannel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val nm = context.getSystemService(NotificationManager::class.java)
                if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                    nm.createNotificationChannel(
                        NotificationChannel(
                            CHANNEL_ID,
                            "Fasting",
                            NotificationManager.IMPORTANCE_DEFAULT
                        )
                    )
                }
            }
        }
    }
}

private data class Quad(
    val id: Int,
    val title: String,
    val text: String,
    val shouldReschedule: Boolean
)
