package com.caloshape.app.data.auth.repo

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.caloshape.app.data.profile.repo.UserProfileStore
import com.caloshape.app.data.water.store.WaterPrefsStore
import com.caloshape.app.data.water.store.WaterUnit
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LocalUserDataPurgerProcessRecreationTest {

    @Test
    fun purge_doesNotRestorePersistentAccountDataAfterStoresAreRecreated() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val tokenStore = TokenStore(context)
        val profileStore = UserProfileStore(context)
        val waterPrefsStore = WaterPrefsStore(context)
        val fastingPreferences = context.getSharedPreferences(
            FASTING_TEMPLATE_PREFS,
            Context.MODE_PRIVATE
        )
        val cacheFile = File(context.cacheDir, "old-account-cache-${System.nanoTime()}.json")

        tokenStore.clear()
        profileStore.clearAllUserData()
        waterPrefsStore.clear()
        fastingPreferences.edit().clear().apply()

        tokenStore.save(access = "old-access-token", refresh = "old-refresh-token")
        profileStore.setGender("female")
        waterPrefsStore.setUnit(WaterUnit.OZ)
        fastingPreferences.edit().putString("template", "old-account-template").apply()
        cacheFile.writeText("old account cache")

        LocalUserDataPurger(
            appContext = context,
            tokenStore = tokenStore,
            profileStore = profileStore,
            waterPrefsStore = waterPrefsStore,
            workoutTodayStore = mockk(relaxed = true),
            fastingAlarmScheduler = mockk(relaxed = true)
        ).purge()

        val recreatedTokenStore = TokenStore(context)
        val recreatedProfileStore = UserProfileStore(context)
        val recreatedWaterStore = WaterPrefsStore(context)

        assertNull(recreatedTokenStore.accessTokenFlow.first())
        assertNull(recreatedTokenStore.refreshTokenFlow.first())
        assertNull(recreatedProfileStore.gender())
        assertTrue(recreatedWaterStore.unitFlow.first() == WaterUnit.ML)
        assertTrue(fastingPreferences.all.isEmpty())
        assertFalse(cacheFile.exists())
    }

    private companion object {
        const val FASTING_TEMPLATE_PREFS = "fasting_notif_templates"
    }
}
