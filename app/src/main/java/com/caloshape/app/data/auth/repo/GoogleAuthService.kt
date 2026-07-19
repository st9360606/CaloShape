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

/** No usable Google credential is available on this device. */
class NoGoogleCredentialAvailableException : Exception()

/** A credential-manager failure that the UI can handle as a sign-in error. */
class CredentialFlowException(message: String) : Exception(message)

class GoogleAuthService(private val context: Context) {

    /** Obtains a Google ID token through Credential Manager. */
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

            val credentialManager = CredentialManager.create(context)
            val response = credentialManager.getCredential(context, request)
            val credential: Credential = response.credential
            if (
                credential is CustomCredential &&
                (
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL ||
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_SIWG_CREDENTIAL
                    )
            ) {
                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                tokenCredential.idToken
                    ?: throw CredentialFlowException("Google did not return an ID token.")
            } else {
                throw CredentialFlowException(
                    "Unexpected credential type: ${credential::class.java.simpleName}"
                )
            }
        } catch (error: NoCredentialException) {
            throw NoGoogleCredentialAvailableException()
        } catch (error: GetCredentialCancellationException) {
            throw error
        } catch (error: GetCredentialException) {
            throw CredentialFlowException(error.errorMessage?.toString() ?: "Google sign-in failed.")
        }
    }
}
