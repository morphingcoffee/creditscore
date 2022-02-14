package com.morphingcoffee.credit_donut.score_display.repository.impl

import com.morphingcoffee.credit_donut.core.model.Result
import com.morphingcoffee.credit_donut.score_display.model.CreditDataDomainError
import com.morphingcoffee.credit_donut.score_display.model.CreditDataResponse
import com.morphingcoffee.credit_donut.score_display.model.CreditReport
import com.morphingcoffee.credit_donut.score_display.model.ICreditDataApiService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class CreditDataRepositoryTest {

    private lateinit var creditDataRepository: CreditDataRepository

    @MockK
    private lateinit var mockApiService: ICreditDataApiService

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        // Instantiate test subject
        creditDataRepository = CreditDataRepository(creditDataApiService = mockApiService)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `correct credit data should be returned on successful retrieval`() {
        // Setup
        val fakeCreditReport = CreditReport(currentScore = 5, maxScore = 10)
        val fakeCreditDataResponse = CreditDataResponse(creditReport = fakeCreditReport)
        coEvery { mockApiService.getCreditData() } returns Response.success(fakeCreditDataResponse)

        runTest {
            // Execute
            val actual: Result<CreditDataResponse, CreditDataDomainError> =
                creditDataRepository.getCreditData()

            // Assert
            coVerify(exactly = 1) {
                mockApiService.getCreditData()
            }
            actual as Result.Value
            assertEquals(fakeCreditDataResponse, actual.value)
        }
    }

    @Test
    fun `error should be returned on failed retrieval`() {
        // Setup
        val fakeHttpCode = 500
        val mockResponseBody: ResponseBody = mockk {
            every { contentType() } returns mockk()
            every { contentLength() } returns 1000
        }
        coEvery { mockApiService.getCreditData() } returns Response.error(
            fakeHttpCode,
            mockResponseBody
        )

        runTest {
            // Execute
            val actual: Result<CreditDataResponse, CreditDataDomainError> =
                creditDataRepository.getCreditData()

            // Assert
            coVerify(exactly = 1) {
                mockApiService.getCreditData()
            }
            actual as Result.Error
            assertEquals(CreditDataDomainError.Unknown, actual.error)
        }
    }

}