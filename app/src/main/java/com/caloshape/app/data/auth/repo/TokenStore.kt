package com.caloshape.app.data.auth.repo

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_tokens")

@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val appContext: Context
) {
    private val KEY_ACCESS = stringPreferencesKey("access")
    private val KEY_REFRESH = stringPreferencesKey("refresh")

    // ?∞Â?ÔºàÈÅ∏?®Ô?ÔºöÂ? access ?∞Ê???epoch ÁßíÊï∏
    private val KEY_ACCESS_EXPIRES_AT = longPreferencesKey("access_expires_at")

    val accessTokenFlow: Flow<String?> = appContext.dataStore.data.safeMap { it[KEY_ACCESS] }
    val refreshTokenFlow: Flow<String?> = appContext.dataStore.data.safeMap { it[KEY_REFRESH] }
    val accessExpiresAtFlow: Flow<Long?> =
        appContext.dataStore.data.safeMap { it[KEY_ACCESS_EXPIRES_AT] }

    /** ‰Ω†Â??¨Á??©Â??∏Á?Ôºà‰??ôÁõ∏ÂÆπÔ? */
    suspend fun save(access: String, refresh: String?) {
        appContext.dataStore.edit {
            it[KEY_ACCESS] = access
            refresh?.let { rt -> it[KEY_REFRESH] = rt }
            it.remove(KEY_ACCESS_EXPIRES_AT) // ?©Â??∏Á?‰∏çÂØ´?∞Ê?Ôºà‰??™Ô?
        }
    }

    /** ?∞Â??õÂ??∏Á?ÔºàÂèØÂ∏∂Âà∞?üÁ??∏Ë?‰º∫Ê??®Ê??ìÁ??∏Ô?Ê≤íÊ?Â∞±ÂøΩ?•Ô? */
    suspend fun save(
        access: String,
        refresh: String?,
        accessExpiresInSec: Long?,
        serverTimeEpochSec: Long?
    ) {
        appContext.dataStore.edit {
            it[KEY_ACCESS] = access
            refresh?.let { rt -> it[KEY_REFRESH] = rt }

            if (accessExpiresInSec != null) {
                val base = serverTimeEpochSec ?: (System.currentTimeMillis() / 1000)
                it[KEY_ACCESS_EXPIRES_AT] = base + accessExpiresInSec
            } else {
                it.remove(KEY_ACCESS_EXPIRES_AT)
            }
        }
    }

    suspend fun clear() {
        appContext.dataStore.edit { it.clear() }
    }

    // Áµ?OkHttp Authenticator Á≠âÂ?Ê≠•Â???
    fun getAccessBlocking(): String? = runBlockingNoCrash { accessTokenFlow.first() }
    fun getRefreshBlocking(): String? = runBlockingNoCrash { refreshTokenFlow.first() }
    fun getAccessExpiresAtBlocking(): Long? = runBlockingNoCrash { accessExpiresAtFlow.first() }

    fun saveBlocking(access: String, refresh: String?) =
        runBlockingNoCrash { save(access, refresh) }

    private fun <T> Flow<Preferences>.safeMap(transform: (Preferences) -> T): Flow<T> =
        catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }.map(transform)

    private fun <T> runBlockingNoCrash(block: suspend () -> T): T? =
        try {
            runBlocking { block() }
        } catch (_: Exception) {
            null
        }
}
