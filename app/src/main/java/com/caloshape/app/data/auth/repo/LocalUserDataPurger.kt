package com.caloshape.app.data.auth.repo

import android.content.Context
import androidx.core.content.edit
import androidx.work.WorkManager
import com.caloshape.app.data.fasting.notifications.FastingAlarmScheduler
import com.caloshape.app.data.profile.repo.UserProfileStore
import com.caloshape.app.data.water.store.WaterPrefsStore
import com.caloshape.app.data.workout.store.WorkoutTodayStore
import com.caloshape.app.widget.CaloShapeWidgetSnapshotStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalUserDataPurger @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val tokenStore: TokenStore,
    private val profileStore: UserProfileStore,
    private val waterPrefsStore: WaterPrefsStore,
    private val workoutTodayStore: WorkoutTodayStore,
    private val fastingAlarmScheduler: FastingAlarmScheduler
) {
    suspend fun purge() {
        runCatching { profileStore.clearAllUserData() }
        runCatching { waterPrefsStore.clear() }
        runCatching { workoutTodayStore.clear() }
        runCatching {
            appContext.getSharedPreferences(FASTING_TEMPLATE_PREFS, Context.MODE_PRIVATE).edit {
                clear()
            }
        }
        runCatching { fastingAlarmScheduler.cancel() }
        runCatching {
            WorkManager.getInstance(appContext).cancelUniqueWork(FASTING_RESCHEDULE_WORK_NAME)
        }
        runCatching { CaloShapeWidgetSnapshotStore.clearAndRefresh(appContext) }
        runCatching {
            appContext.cacheDir.listFiles()?.forEach { cachedFile ->
                cachedFile.deleteRecursively()
            }
        }

        tokenStore.clear()
    }

    private companion object {
        const val FASTING_TEMPLATE_PREFS = "fasting_notif_templates"
        const val FASTING_RESCHEDULE_WORK_NAME = "fasting_reschedule"
    }
}
