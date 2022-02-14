package com.morphingcoffee.credit_donut.score_display.model

/** Class encapsulating different user interaction events for Score Display feature **/
sealed class ScoreDisplayUserAction {
    object ReloadScore : ScoreDisplayUserAction()
}