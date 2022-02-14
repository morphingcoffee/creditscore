package com.morphingcoffee.credit_donut.score_display.view

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.morphingcoffee.credit_donut.R
import com.morphingcoffee.credit_donut.core.model.Result
import com.morphingcoffee.credit_donut.score_display.model.CreditDataDomainError
import com.morphingcoffee.credit_donut.score_display.model.CreditDataResponse
import com.morphingcoffee.credit_donut.score_display.model.CreditReport
import com.morphingcoffee.credit_donut.score_display.repository.ICreditDataRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin

/** Fragment-ViewModel Integration UI Test **/
@ExperimentalCoroutinesApi
class CreditScoreFragmentTest {

    @MockK
    lateinit var creditDataRepository: ICreditDataRepository

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        // Mock DI dependencies
        startKoin {
            allowOverride(true)
            modules(
                module {
                    viewModel {
                        CreditScoreViewModel(creditDataRepository)
                    }
                }
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun pendingCreditScoreRetrieval_shouldResultIn_progressIndicationDisplayedToTheUser() {
        // Set up with test dispatcher. For some reason other scenarios won't complete properly with same technique.
        getKoin().loadModules(listOf(module {
            viewModel {
                CreditScoreViewModel(
                    creditDataRepository,
                    defaultDispatcher = dispatcher
                )
            }
        }))

        runTest {
            // Launch fragment
            launchFragmentInContainer<CreditScoreFragment>()

            // Assert
            onView(withText("Getting your credit score")).check(matches(isDisplayed()))
            onView(withText("?")).check(matches(isDisplayed()))
        }
    }

    @Test
    fun successfulCreditScoreRetrieval_shouldResultIn_scoreInformationDisplayedToTheUser() {
        // Mock dependencies
        val fakeReport = CreditReport(currentScore = 5, maxScore = 10)
        val fakeResponse = CreditDataResponse(fakeReport)
        coEvery { creditDataRepository.getCreditData() } returns Result.Value(fakeResponse)

        runTest {
            // Launch fragment
            launchFragmentInContainer<CreditScoreFragment>()

            // Assert
            coVerify(exactly = 1) { creditDataRepository.getCreditData() }
            onView(withText("Your credit score is")).check(matches(isDisplayed()))
            onView(withText("5")).check(matches(isDisplayed()))
            onView(withText("out of 10")).check(matches(isDisplayed()))
        }
    }

    @Test
    fun failedCreditScoreRetrieval_shouldResultIn_errorMessageDisplayedToTheUser() {
        // Mock dependencies
        coEvery { creditDataRepository.getCreditData() } returns Result.Error(CreditDataDomainError.NoInternet)

        runTest {
            // Launch fragment
            launchFragmentInContainer<CreditScoreFragment>()

            // Assert
            coVerify(exactly = 1) { creditDataRepository.getCreditData() }
            onView(withText("We're having issues")).check(matches(isDisplayed()))
            onView(withText("!")).check(matches(isDisplayed()))
            onView(withText("Touch to try again")).check(matches(isDisplayed()))
        }
    }

    @Test
    fun userRetryAction_shouldResultIn_creditDataRequest() {
        // Mock dependencies
        coEvery { creditDataRepository.getCreditData() } returns Result.Error(CreditDataDomainError.NoInternet)

        runTest {
            // Launch fragment
            launchFragmentInContainer<CreditScoreFragment>()

            // Assert failure scenario
            onView(withText("We're having issues")).check(matches(isDisplayed()))
            onView(withText("!")).check(matches(isDisplayed()))
            onView(withText("Touch to try again")).check(matches(isDisplayed()))

            // Prepare fake successful credit report
            val fakeReport = CreditReport(currentScore = 5, maxScore = 10)
            val fakeResponse = CreditDataResponse(fakeReport)
            coEvery { creditDataRepository.getCreditData() } returns Result.Value(fakeResponse)

            // Imitate user retry action
            onView(withId(R.id.content)).perform(click())

            // Assert success scenario
            onView(withText("Your credit score is")).check(matches(isDisplayed()))
            onView(withText("5")).check(matches(isDisplayed()))
            onView(withText("out of 10")).check(matches(isDisplayed()))
        }
    }

}