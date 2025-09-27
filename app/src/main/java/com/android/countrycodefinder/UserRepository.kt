package com.android.countrycodefinder

import com.android.countrycodefinder.datamodels.DataClass
import com.android.countrycodefinder.network.Retrofit

class UserRepository {
    suspend fun fetchCountries(): List<DataClass>? {
        val response = Retrofit.instance.getCountries()
        return if (response.isSuccessful) {
            response.body()?.data
        } else {
            null
        }
    }
}
