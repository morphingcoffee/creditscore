package com.morphingcoffee.credit_donut.score_display.model

sealed class ScoreDisplayState {
    /** Score retrieval in progress **/
    object Pending : ScoreDisplayState()

    /** Score is ready to be displayed **/
    data class Loaded(val currentScore: Int, val totalScore: Int, val percent: Float) :
        ScoreDisplayState()

    /** An error occurred while retrieving the score **/
    data class Error(val reason: CreditDataDomainError) : ScoreDisplayState()
}