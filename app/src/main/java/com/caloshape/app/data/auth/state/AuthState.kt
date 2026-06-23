package com.caloshape.app.data.auth.state

import com.caloshape.app.data.auth.repo.TokenStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Â∞ÅË??ªÂÖ•?Ä?ãÔ???TokenStore ??accessToken ?ØÂê¶Â≠òÂú®?§Êñ∑??
 */
@Singleton
class AuthState @Inject constructor(
    tokenStore: TokenStore
) {
    val isSignedInFlow: Flow<Boolean> =
        tokenStore.accessTokenFlow.map { !it.isNullOrBlank() }
}
