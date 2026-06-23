// app/src/main/java/com/caloshape/app/data/auth/GoogleAuthService.kt
package com.caloshape.app.data.auth

import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.caloshape.app.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** 裝置沒�?任�??�用??Google ?��?（�??�入帳�??�未?��?�?*/
class NoGoogleCredentialAvailableException : Exception()

/** ?��??��?流�??�通用?�誤（避??IllegalStateException ?�歧義�? */
class CredentialFlowException(message: String) : Exception(message)

class GoogleAuthService(private val context: Context) {

    /** �?Credential Manager ??Google ID Token；若?��?證�???NoGoogleCredentialAvailableException */
    suspend fun getIdToken(): String = withContext(Dispatchers.Main) {
        try {
            val serverClientId = context.getString(R.string.google_web_client_id)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(serverClientId)
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val cm = CredentialManager.create(context)
            val response = cm.getCredential(context, request) //?�到google login

            val cred: Credential = response.credential
            if (cred is CustomCredential &&
                cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val tokenCred = GoogleIdTokenCredential.createFrom(cred.data)
                tokenCred.idToken ?: throw CredentialFlowException("Google ?�傳空�? ID Token")
            } else {
                throw CredentialFlowException("Unexpected credential type: ${cred::class.java.simpleName}")
            }
        } catch (e: NoCredentialException) {
            throw NoGoogleCredentialAvailableException()
        } catch (e: GetCredentialCancellationException) {
            throw e // 交由上層顯示?�已?��???
        } catch (e: GetCredentialException) {
            throw CredentialFlowException((e.errorMessage ?: "?��??��?失�?") as String)
        }
    }
}
