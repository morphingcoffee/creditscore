package com.morphingcoffee.credit_donut.score_display.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** Data holder class for credit report **/
@JsonClass(generateAdapter = true)
data class CreditReport(
    @Json(name = "score") val currentScore: Int,
    @Json(name = "maxScoreValue") val maxScore: Int
)