package com.android.countrycodefinder.network

import com.android.countrycodefinder.datamodels.ResponseClass
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    @GET("api/v1/country")
    suspend fun getCountries(): Response<ResponseClass>
}


