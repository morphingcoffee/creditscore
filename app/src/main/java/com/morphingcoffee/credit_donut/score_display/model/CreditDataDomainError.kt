package com.morphingcoffee.credit_donut.score_display.model

import androidx.annotation.StringRes
import com.morphingcoffee.credit_donut.R

/** Class encapsulating different errors for retrieving credit score **/
sealed class CreditDataDomainError(@StringRes val clientFacingErrorMessage: Int) {
    object NoInternet : CreditDataDomainError(R.string.error_message_no_internet)
    object Unknown : CreditDataDomainError(R.string.error_message_unknown)
}