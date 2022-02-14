package com.morphingcoffee.credit_donut.score_display.repository.impl

import com.morphingcoffee.credit_donut.core.model.Result
import com.morphingcoffee.credit_donut.score_display.model.CreditDataDomainError
import com.morphingcoffee.credit_donut.score_display.model.CreditDataResponse
import com.morphingcoffee.credit_donut.score_display.model.ICreditDataApiService
import com.morphingcoffee.credit_donut.score_display.repository.ICreditDataRepository

class CreditDataRepository(private val creditDataApiService: ICreditDataApiService) :
    ICreditDataRepository {

    override suspend fun getCreditData(): Result<CreditDataResponse, CreditDataDomainError> {
        val creditData = creditDataApiService.getCreditData()
        val body = creditData.body()
        return if (creditData.isSuccessful && body != null) {
            Result.Value(body)
        } else {
            // Could handle more granular error scenarios based on [creditData] response metadata if desired
            Result.Error(CreditDataDomainError.Unknown)
        }
    }

}