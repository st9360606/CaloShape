package com.caloshape.app.data.auth.api

import com.caloshape.app.data.auth.api.model.AuthResponse
import com.caloshape.app.data.auth.api.model.GoogleSignInExchangeRequest
import com.caloshape.app.data.auth.api.model.RefreshRequest
import com.caloshape.app.data.auth.api.model.StartEmailReq
import com.caloshape.app.data.auth.api.model.StartEmailRes
import com.caloshape.app.data.auth.api.model.VerifyEmailReq
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Header

interface AuthApi {
    @POST("auth/google")
    suspend fun googleLogin(@Body body: GoogleSignInExchangeRequest): AuthResponse

    @POST("auth/refresh")
    fun refresh(@Body body: RefreshRequest): Call<AuthResponse> // Authenticator ?ÄË¶ÅÂ?Ê≠?Call

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") authorization: String?,
        @Body body: RefreshRequest?
    )

    @POST("auth/email/start")
    suspend fun startEmail(@Body body: StartEmailReq): StartEmailRes

    /** Â§æÂ∏∂ X-Device-IdÔºàÂèØ??nullÔºâÔ?ÂæåÁ´Ø?ÉË??ÑÂú® token ÂØ©Ë? */
    @POST("auth/email/verify")
    suspend fun verifyEmail(
        @Body body: VerifyEmailReq,
        @Header("X-Device-Id") deviceId: String? = null
    ): AuthResponse
}
