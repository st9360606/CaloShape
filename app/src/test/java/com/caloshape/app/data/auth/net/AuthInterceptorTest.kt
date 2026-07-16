package com.caloshape.app.data.auth.net

import com.caloshape.app.data.auth.repo.TokenStore
import io.mockk.mockk
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthInterceptorTest {

    private val interceptor = AuthInterceptor(mockk(relaxed = true))
    private val trusted = "https://api.caloshape.com/".toHttpUrl()

    @Test
    fun `same origin is trusted`() {
        assertTrue(
            interceptor.isTrustedApiOrigin(
                "https://api.caloshape.com/api/v1/weights/photos/photo.jpg".toHttpUrl(),
                trusted
            )
        )
    }

    @Test
    fun `lookalike and external hosts are not trusted`() {
        assertFalse(
            interceptor.isTrustedApiOrigin(
                "https://api.caloshape.com.evil.example/photo.jpg".toHttpUrl(),
                trusted
            )
        )
        assertFalse(
            interceptor.isTrustedApiOrigin(
                "https://images.example.com/avatar.jpg".toHttpUrl(),
                trusted
            )
        )
    }

    @Test
    fun `scheme and port must also match`() {
        assertFalse(
            interceptor.isTrustedApiOrigin(
                "http://api.caloshape.com/photo.jpg".toHttpUrl(),
                trusted
            )
        )
        assertFalse(
            interceptor.isTrustedApiOrigin(
                "https://api.caloshape.com:8443/photo.jpg".toHttpUrl(),
                trusted
            )
        )
    }
}
