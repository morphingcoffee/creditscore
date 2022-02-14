package com.morphingcoffee.credit_donut.score_display.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morphingcoffee.credit_donut.core.model.Result
import com.morphingcoffee.credit_donut.score_display.model.CreditDataDomainError
import com.morphingcoffee.credit_donut.score_display.model.CreditDataResponse
import com.morphingcoffee.credit_donut.score_display.model.ScoreDisplayState
import com.morphingcoffee.credit_donut.score_display.model.ScoreDisplayUserAction
import com.morphingcoffee.credit_donut.score_display.repository.ICreditDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException

/**
 * View Model providing Credit Score [state]
 * and accepting [ScoreDisplayUserAction] to [handleUserAction]
 *
 * Automatically kicks off credit score retrieval on construction.
 **/
class CreditScoreViewModel(
    private val creditDataRepository: ICreditDataRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _state = MutableLiveData<ScoreDisplayState>(ScoreDisplayState.Pending)

    /** Latest [ScoreDisplayState] state. Defaults to [ScoreDisplayState.Pending] **/
    val state: LiveData<ScoreDisplayState> = _state

    init {
        // Trigger initial data lookup
        fetch()
    }

    /** React to different user interaction events **/
    fun handleUserAction(action: ScoreDisplayUserAction) {
        when (action) {
            ScoreDisplayUserAction.ReloadScore -> fetch()
        }
    }

    private fun fetch() {
        // Broadcast pending progress while data is fetched from repository
        _state.value = ScoreDisplayState.Pending
        viewModelScope.launch(defaultDispatcher) {
            try {
                creditDataRepository.getCreditData()
                    .also(this@CreditScoreViewModel::handleCreditDataResult)
            } catch (e: IOException) {
                handleError(CreditDataDomainError.NoInternet)
            } catch (e: Exception) {
                handleError(CreditDataDomainError.Unknown)
            }
        }
    }

    private fun handleCreditDataResult(result: Result<CreditDataResponse, CreditDataDomainError>) {
        when (result) {
            is Result.Value -> handleCreditDataResponse(result.value)
            is Result.Error -> handleError(result.error)
        }
    }

    private fun handleCreditDataResponse(response: CreditDataResponse) {
        val report = response.creditReport
        val percent = (report.currentScore.toFloat() / report.maxScore) * 100
        _state.value = ScoreDisplayState.Loaded(
            report.currentScore,
            report.maxScore,
            percent
        )
    }

    private fun handleError(reason: CreditDataDomainError) {
        // Could handle different errors based on [reason] differently if desired
        _state.value = ScoreDisplayState.Error(reason)
    }

}