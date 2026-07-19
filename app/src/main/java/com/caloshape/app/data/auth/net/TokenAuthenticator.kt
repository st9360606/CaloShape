package com.caloshape.app.data.auth.net

import com.caloshape.app.data.auth.api.AuthApi
import com.caloshape.app.data.auth.api.model.RefreshRequest
import com.caloshape.app.data.auth.repo.TokenStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Named


object SessionBus {
    private val _expired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val expired = _expired.asSharedFlow()
    fun emitExpired() { _expired.tryEmit(Unit) }
}

class TokenAuthenticator @Inject constructor(
    private val tokenStore: TokenStore,
    @Named("authApi") private val authApi: AuthApi
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {

        if (responseCount(response) >= 2) return null

        synchronized(lock) {

            tokenStore.getAccessBlocking()?.let { existing ->
                val authOnReq = response.request.header("Authorization")
                if (authOnReq != "Bearer $existing" && existing.isNotBlank()) {
                    return response.request.newBuilder()
                        .header("Authorization", "Bearer $existing")
                        .build()
                }
            }

            val refresh = tokenStore.getRefreshBlocking()
                ?.takeIf { it.isNotBlank() }
                ?: return failHard()

            val resp = try {
                authApi.refresh(
                    RefreshRequest(
                        refreshToken = refresh,
                        deviceId = null
                    )
                ).execute()
            } catch (t: Throwable) {

                return failSoft()
            }

            if (resp.isSuccessful) {
                val body = resp.body() ?: return failSoft()

                val newAccess = body.accessToken.takeIf { it.isNotBlank() } ?: return failSoft()
                val newRefresh = body.refreshToken?.takeIf { it.isNotBlank() } ?: refresh

                tokenStore.saveBlocking(newAccess, newRefresh)

                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccess")
                    .build()
            }

            return when (resp.code()) {
                400, 401, 403 -> failHard()
                else -> failSoft()
            }
        }
    }

    private fun failHard(): Request? {

        tokenStore.saveBlocking(access = "", refresh = null)
        SessionBus.emitExpired()
        return null
    }

    private fun failSoft(): Request? {

        return null
    }

    private fun responseCount(response: Response): Int {
        var r: Response? = response
        var count = 1
        while (r?.priorResponse != null) {
            count++
            r = r.priorResponse
        }
        return count
    }
}
