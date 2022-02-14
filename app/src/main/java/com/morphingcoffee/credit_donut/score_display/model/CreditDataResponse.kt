package com.morphingcoffee.credit_donut.score_display.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/** Data holder class for credit data response **/
@JsonClass(generateAdapter = true)
data class CreditDataResponse(@Json(name = "creditReportInfo") val creditReport: CreditReport)