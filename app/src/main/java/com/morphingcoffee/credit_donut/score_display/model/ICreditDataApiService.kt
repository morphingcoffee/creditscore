package com.morphingcoffee.credit_donut.score_display.model

import retrofit2.Response
import retrofit2.http.GET

interface ICreditDataApiService {
    /** Fetches [CreditDataResponse] from the server **/
    @GET("endpoint.json")
    suspend fun getCreditData(): Response<CreditDataResponse>
}