package com.caloshape.app.data.auth.repo

import com.caloshape.app.data.auth.api.AuthApi
import com.caloshape.app.data.auth.api.model.AuthResponse
import com.caloshape.app.data.auth.api.model.GoogleSignInExchangeRequest
import com.caloshape.app.data.auth.api.model.RefreshRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    @Named("authApi") private val api: AuthApi,   // ???áÂ? auth Â∞àÁî® Retrofit
    private val tokenStore: TokenStore
) {
    suspend fun loginWithGoogle(idToken: String, clientId: String? = null): AuthResponse {
        val resp = api.googleLogin(
            GoogleSignInExchangeRequest(idToken = idToken, clientId = clientId)
        )
        // ‰Ω†ÁõÆ?çÁ? AuthResponse ?•Ê???expiresIn / serverTimeÔºåÂèØ?àÁî®?©Â??∏Á???
        tokenStore.save(resp.accessToken, resp.refreshToken)
        return resp
    }

    suspend fun logout() {
        try {
            logoutRemote()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // Keep legacy behavior: local sign-out must still complete if the remote call fails.
        }
        tokenStore.clear()
    }

    suspend fun logoutRemoteThenClear(): Result<Unit> =
        try {
            logoutRemote()
            tokenStore.clear()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }

    private suspend fun logoutRemote() {
        val access = tokenStore.accessTokenFlow.firstOrNull()
        val refresh = tokenStore.refreshTokenFlow.firstOrNull()
        if (access.isNullOrBlank() && refresh.isNullOrBlank()) return

        api.logout(
            authorization = access?.takeIf { it.isNotBlank() }?.let { "Bearer $it" },
            body = refresh?.takeIf { it.isNotBlank() }?.let { RefreshRequest(it) }
        )
    }

    /**
     * ?ØÂê¶Â∑≤Áôª?•Ô?
     * - access token ‰∏çÁÇ∫Á©?
     * - ‰∏îÔ?Â¶ÇÊ?Ë®≠Â?ÔºâÊú™?éÊ?ÔºàÂ? 5 ÁßíÁ∑©Ë°ùÈÅø?çËá®?åÈ?Ôº?
     */
    suspend fun isSignedIn(): Boolean {
        val access = tokenStore.accessTokenFlow.firstOrNull()
        if (access.isNullOrBlank()) return false

        val expiresAtSec = tokenStore.accessExpiresAtFlow.firstOrNull()
        val nowSec = System.currentTimeMillis() / 1000
        // ?•Ê??âË??ÑÂà∞?üÊ??ìÔ?Â∞±‰ª•?åÂ???access token?çË??∫Â∑≤?ªÂÖ•
        return expiresAtSec == null || expiresAtSec > (nowSec + 5)
    }
}
