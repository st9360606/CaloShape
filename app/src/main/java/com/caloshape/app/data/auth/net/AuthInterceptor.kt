package com.caloshape.app.data.auth.net

import com.caloshape.app.BuildConfig
import com.caloshape.app.data.auth.repo.TokenStore
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrl
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val access = tokenStore.getAccessBlocking()
        val req = if (!access.isNullOrBlank() && isTrustedApiOrigin(original.url)) {
            original.newBuilder()
                .header("Authorization", "Bearer $access")
                .build()
        } else original
        return chain.proceed(req)
    }

    internal fun isTrustedApiOrigin(
        requestUrl: HttpUrl,
        trustedBaseUrl: HttpUrl = BuildConfig.BASE_URL.toHttpUrl()
    ): Boolean =
        requestUrl.scheme == trustedBaseUrl.scheme &&
                requestUrl.host == trustedBaseUrl.host &&
                requestUrl.port == trustedBaseUrl.port
}
