package com.caloshape.app.data.entitlement.net

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntitlementConflictResponseInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (request.url.encodedPath == ENTITLEMENT_SYNC_PATH && !response.isSuccessful) {
            val errorCode = if (response.containsPurchaseTokenAlreadyBoundCode()) {
                PURCHASE_TOKEN_ALREADY_BOUND
            } else {
                ENTITLEMENT_SYNC_FAILED
            }
            response.close()
            throw IOException(errorCode)
        }

        return response
    }

    private fun Response.containsPurchaseTokenAlreadyBoundCode(): Boolean {
        val responseBody = peekBody(MAX_ERROR_BODY_BYTES).string()
        if (responseBody.isBlank()) return false

        return runCatching {
            val body = Json.parseToJsonElement(responseBody).jsonObject
            ERROR_CODE_FIELDS.any { field ->
                body[field]
                    ?.jsonPrimitive
                    ?.contentOrNull
                    ?.equals(PURCHASE_TOKEN_ALREADY_BOUND, ignoreCase = true) == true
            }
        }.getOrDefault(false)
    }

    internal companion object {
        const val ENTITLEMENT_SYNC_PATH = "/api/v1/entitlements/sync"
        const val PURCHASE_TOKEN_ALREADY_BOUND = "PURCHASE_TOKEN_ALREADY_BOUND"
        const val ENTITLEMENT_SYNC_FAILED = "ENTITLEMENT_SYNC_FAILED"
        private const val MAX_ERROR_BODY_BYTES = 64L * 1024L
        private val ERROR_CODE_FIELDS = setOf("code", "errorCode", "message", "rootMessage")
    }
}
