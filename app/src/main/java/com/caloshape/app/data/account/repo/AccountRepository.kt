// app/src/main/java/com/caloshape/app/data/account/repo/AccountRepository.kt
package com.caloshape.app.data.account.repo

import com.caloshape.app.data.account.api.AccountApi
import com.caloshape.app.data.account.api.AccountDeletionRequest
import com.caloshape.app.data.auth.repo.LocalUserDataPurger
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val api: AccountApi,
    private val localUserDataPurger: LocalUserDataPurger,
) {

    /**
     * Account deletion must be confirmed by the backend before local auth is cleared.
     *
     * Do not treat 401/403 as success here. If the token is expired or invalid,
     * the deletion request may not have been created on the server. The UI should
     * ask the user to sign in again and retry the deletion request.
     */
    suspend fun deleteAccount(
        subscriptionWarningAcknowledged: Boolean = false,
        userRequestedGooglePlayCancel: Boolean = false
    ): Result<Unit> {
        return runCatching {
            val res = api.requestDeletion(
                AccountDeletionRequest(
                    subscriptionWarningAcknowledged = subscriptionWarningAcknowledged,
                    userRequestedGooglePlayCancel = userRequestedGooglePlayCancel
                )
            )
            if (!res.ok) throw IllegalStateException("DELETE_ACCOUNT_FAILED")

            localUserDataPurger.purge()
            Unit
        }.recoverCatching { e ->
            if (e is HttpException && (e.code() == 401 || e.code() == 403)) {
                throw IllegalStateException("Please sign in again before deleting your account.", e)
            }

            if (e is HttpException && e.code() == 409) {
                throw IllegalStateException(
                    "Please manage your Google Play subscription, then try deleting your account again.",
                    e
                )
            }

            throw e
        }
    }

    suspend fun getDeletionPreview() = runCatching {
        api.deletionPreview()
    }
}
