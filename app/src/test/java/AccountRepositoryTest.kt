import com.caloshape.app.data.account.api.AccountApi
import com.caloshape.app.data.account.repo.AccountRepository
import com.caloshape.app.data.auth.repo.LocalUserDataPurger
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import retrofit2.Retrofit
import retrofit2.create
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

class AccountRepositoryTest {

    private lateinit var server: MockWebServer
    private lateinit var api: AccountApi

    private lateinit var localUserDataPurger: LocalUserDataPurger

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        val json = Json { ignoreUnknownKeys = true }

        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create()

        localUserDataPurger = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun deleteAccount_when200_ok_should_clear_local_and_success() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"ok":true}""")
        )

        val repo = AccountRepository(api, localUserDataPurger)

        val r = repo.deleteAccount()

        assertTrue(r.isSuccess)

        coVerify(exactly = 1) { localUserDataPurger.purge() }
    }

    @Test
    fun deleteAccount_when401_should_fail_without_clearing_local_auth() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"code":"UNAUTHORIZED"}""")
        )

        val repo = AccountRepository(api, localUserDataPurger)

        val r = repo.deleteAccount()

        assertTrue(r.isFailure)

        coVerify(exactly = 0) { localUserDataPurger.purge() }
    }

    @Test
    fun deleteAccount_when403_should_fail_without_clearing_local_auth() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(403)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"code":"FORBIDDEN"}""")
        )

        val repo = AccountRepository(api, localUserDataPurger)

        val r = repo.deleteAccount()

        assertTrue(r.isFailure)

        coVerify(exactly = 0) { localUserDataPurger.purge() }
    }

    @Test
    fun deleteAccount_when500_should_fail() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"code":"INTERNAL"}""")
        )

        val repo = AccountRepository(api, localUserDataPurger)

        val r = repo.deleteAccount()

        assertFalse(r.isSuccess)
        assertTrue(r.isFailure)
        coVerify(exactly = 0) { localUserDataPurger.purge() }
    }
}
