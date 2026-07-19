package com.caloshape.app.data.auth.repo

import android.content.Context
import android.content.SharedPreferences
import com.caloshape.app.data.fasting.notifications.FastingAlarmScheduler
import com.caloshape.app.data.profile.repo.UserProfileStore
import com.caloshape.app.data.water.store.WaterPrefsStore
import com.caloshape.app.data.workout.store.WorkoutTodayStore
import com.caloshape.app.widget.CaloShapeWidgetSnapshotStore
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkObject
import io.mockk.verify
import java.nio.file.Files
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class LocalUserDataPurgerTest {
    private lateinit var context: Context
    private lateinit var tokenStore: TokenStore
    private lateinit var profileStore: UserProfileStore
    private lateinit var waterPrefsStore: WaterPrefsStore
    private lateinit var workoutTodayStore: WorkoutTodayStore
    private lateinit var fastingAlarmScheduler: FastingAlarmScheduler
    private lateinit var cacheDirectory: java.io.File

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        tokenStore = mockk(relaxed = true)
        profileStore = mockk(relaxed = true)
        waterPrefsStore = mockk(relaxed = true)
        workoutTodayStore = mockk(relaxed = true)
        fastingAlarmScheduler = mockk(relaxed = true)
        cacheDirectory = Files.createTempDirectory("local-user-data-purger").toFile()
        Files.write(
            cacheDirectory.toPath().resolve("old-account-cache.json"),
            "old account".toByteArray()
        )

        val preferences = mockk<SharedPreferences>()
        val editor = mockk<SharedPreferences.Editor>()
        every { context.cacheDir } returns cacheDirectory
        every { context.getSharedPreferences(any(), any()) } returns preferences
        every { preferences.edit() } returns editor
        every { editor.clear() } returns editor
        every { editor.apply() } just runs

        mockkObject(CaloShapeWidgetSnapshotStore)
        every { CaloShapeWidgetSnapshotStore.clearAndRefresh(context) } just runs
    }

    @After
    fun tearDown() {
        unmockkObject(CaloShapeWidgetSnapshotStore)
        cacheDirectory.deleteRecursively()
    }

    @Test
    fun purge_clearsLocalStoresWidgetFastingAndCacheBeforeTokenRemoval() = runTest {
        LocalUserDataPurger(
            appContext = context,
            tokenStore = tokenStore,
            profileStore = profileStore,
            waterPrefsStore = waterPrefsStore,
            workoutTodayStore = workoutTodayStore,
            fastingAlarmScheduler = fastingAlarmScheduler
        ).purge()

        coVerify(exactly = 1) { profileStore.clearAllUserData() }
        coVerify(exactly = 1) { waterPrefsStore.clear() }
        verify(exactly = 1) { workoutTodayStore.clear() }
        verify(exactly = 1) { fastingAlarmScheduler.cancel() }
        verify(exactly = 1) { CaloShapeWidgetSnapshotStore.clearAndRefresh(context) }
        coVerify(exactly = 1) { tokenStore.clear() }
        assertFalse(cacheDirectory.resolve("old-account-cache.json").exists())
    }
}
