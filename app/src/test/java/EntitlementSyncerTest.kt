import android.app.Activity
import com.caloshape.app.data.billing.ActiveSub
import com.caloshape.app.data.billing.BillingGateway
import com.caloshape.app.data.billing.BillingPurchaseResult
import com.caloshape.app.data.billing.SubscriptionOfferPriceText
import com.caloshape.app.data.entitlement.EntitlementSyncer
import com.caloshape.app.data.entitlement.api.EntitlementApi
import com.caloshape.app.data.entitlement.api.EntitlementSyncResponse
import com.caloshape.app.data.membership.api.MembershipApi
import com.caloshape.app.data.membership.api.MembershipSummaryDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Test

class EntitlementSyncerTest {

    @Test
    fun sync_whenNoSubs_shouldNotCallSyncButShouldCallMe() = runBlocking {
        val billing = FakeBillingGateway(
            activeSubs = emptyList()
        )

        val api = mockk<EntitlementApi>()
        val membershipApi = mockk<MembershipApi>()

        coEvery { api.me() } returns EntitlementSyncResponse(
            status = "INACTIVE",
            entitlementType = null,
            premiumStatus = "FREE"
        )
        coEvery { membershipApi.me() } returns MembershipSummaryDto(
            premiumStatus = "FREE"
        )

        val syncer = EntitlementSyncer(billing, api, membershipApi)

        syncer.syncAfterLoginSilently()

        coVerify(exactly = 0) {
            api.sync(any())
        }

        coVerify(exactly = 1) {
            api.me()
        }
    }

    @Test
    fun sync_whenDeviceHasSubs_shouldStillUseServerSummaryWithoutPlaySync() = runBlocking {
        val billing = FakeBillingGateway(
            activeSubs = listOf(
                ActiveSub(
                    productId = "monthly",
                    purchaseToken = "tok123",
                    acknowledged = true
                )
            )
        )

        val api = mockk<EntitlementApi>()
        val membershipApi = mockk<MembershipApi>()

        coEvery { api.me() } returns EntitlementSyncResponse(
            status = "ACTIVE",
            entitlementType = "MONTHLY",
            premiumStatus = "PREMIUM",
            currentPremiumUntil = "2026-12-31T00:00:00Z"
        )
        coEvery { membershipApi.me() } returns MembershipSummaryDto(
            premiumStatus = "PREMIUM",
            currentPremiumUntil = "2026-12-31T00:00:00Z"
        )

        val syncer = EntitlementSyncer(billing, api, membershipApi)

        syncer.syncAfterLoginSilently()

        coVerify(exactly = 0) {
            api.sync(any())
        }

        coVerify(exactly = 1) {
            api.me()
        }
    }

    private class FakeBillingGateway(
        private val activeSubs: List<ActiveSub> = emptyList()
    ) : BillingGateway {

        override suspend fun queryActiveSubscriptions(): List<ActiveSub> {
            return activeSubs
        }

        override suspend fun querySubscriptionOfferPrice(
            productId: String,
            offerTag: String?
        ): SubscriptionOfferPriceText? {
            return null
        }

        override suspend fun launchSubscriptionPurchase(
            activity: Activity,
            productId: String,
            offerTag: String?
        ): BillingPurchaseResult {
            return BillingPurchaseResult.Error("Not used in EntitlementSyncerTest")
        }

        override suspend fun acknowledgePurchase(
            purchaseToken: String
        ): Boolean {
            return true
        }
    }
}
