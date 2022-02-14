package com.morphingcoffee.credit_donut.score_display.repository

import com.morphingcoffee.credit_donut.core.model.Result
import com.morphingcoffee.credit_donut.score_display.model.CreditDataDomainError
import com.morphingcoffee.credit_donut.score_display.model.CreditDataResponse

interface ICreditDataRepository {
    /**
     * Retrieves result containing [CreditDataResponse]
     * on success or [CreditDataDomainError] on failure
     * **/
    suspend fun getCreditData(): Result<CreditDataResponse, CreditDataDomainError>
}