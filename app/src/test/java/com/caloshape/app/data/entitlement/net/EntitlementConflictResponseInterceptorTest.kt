package com.caloshape.app.data.entitlement.net

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import java.io.IOException

class EntitlementConflictResponseInterceptorTest {

    private lateinit var server: MockWebServer
    private lateinit var client: OkHttpClient

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        client = OkHttpClient.Builder()
            .addInterceptor(EntitlementConflictResponseInterceptor())
            .build()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `entitlement sync conflict with structured code preserves purchase bound code`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(409)
                .setHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "code": "PURCHASE_TOKEN_ALREADY_BOUND",
                      "message": "PURCHASE_TOKEN_ALREADY_BOUND"
                    }
                    """.trimIndent()
                )
        )

        val exception = assertThrows(IOException::class.java) {
            client.newCall(
                Request.Builder()
                    .url(server.url("/api/v1/entitlements/sync"))
                    .build()
            ).execute()
        }

        assertEquals(
            EntitlementConflictResponseInterceptor.PURCHASE_TOKEN_ALREADY_BOUND,
            exception.message
        )
    }

    @Test
    fun `dev server error with root message preserves purchase bound code`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "type": "java.lang.IllegalStateException",
                      "message": "PURCHASE_TOKEN_ALREADY_BOUND",
                      "rootMessage": "PURCHASE_TOKEN_ALREADY_BOUND"
                    }
                    """.trimIndent()
                )
        )

        val exception = assertThrows(IOException::class.java) {
            client.newCall(
                Request.Builder()
                    .url(server.url("/api/v1/entitlements/sync"))
                    .build()
            ).execute()
        }

        assertEquals(
            EntitlementConflictResponseInterceptor.PURCHASE_TOKEN_ALREADY_BOUND,
            exception.message
        )
    }

    @Test
    fun `unrelated entitlement conflict becomes generic sync failure`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(409)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"code":"DIFFERENT_CONFLICT"}""")
        )

        val exception = assertThrows(IOException::class.java) {
            client.newCall(
                Request.Builder()
                    .url(server.url("/api/v1/entitlements/sync"))
                    .build()
            ).execute()
        }

        assertEquals(
            EntitlementConflictResponseInterceptor.ENTITLEMENT_SYNC_FAILED,
            exception.message
        )
    }

    @Test
    fun `server error without domain code becomes generic sync failure`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"code":"INTERNAL_ERROR"}""")
        )

        val exception = assertThrows(IOException::class.java) {
            client.newCall(
                Request.Builder()
                    .url(server.url("/api/v1/entitlements/sync"))
                    .build()
            ).execute()
        }

        assertEquals(
            EntitlementConflictResponseInterceptor.ENTITLEMENT_SYNC_FAILED,
            exception.message
        )
    }

    @Test
    fun `same error code on unrelated endpoint remains an http response`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(409)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"code":"PURCHASE_TOKEN_ALREADY_BOUND"}""")
        )

        val response = client.newCall(
            Request.Builder()
                .url(server.url("/api/v1/other"))
                .build()
        ).execute()

        response.use {
            assertEquals(409, it.code)
        }
    }
}
