package com.caloshape.app.ui.appearance

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.appearanceDataStore by preferencesDataStore(name = "appearance_prefs")

class AppearanceStore(private val context: Context) {
    val modeFlow: Flow<AppearanceMode> = context.appearanceDataStore.data.map { prefs ->
        AppearanceMode.fromStored(prefs[KEY_APPEARANCE_MODE])
    }

    suspend fun setMode(mode: AppearanceMode) {
        context.appearanceDataStore.edit { prefs ->
            prefs[KEY_APPEARANCE_MODE] = mode.name
        }
    }

    private companion object {
        val KEY_APPEARANCE_MODE = stringPreferencesKey("appearance_mode")
    }
}
