package com.challenge.listadeciudades.data.repository

import com.challenge.listadeciudades.data.remote.WikipediaApiService
import com.challenge.listadeciudades.data.remote.model.WikipediaResponse

class WikipediaRepository(
    private val api: WikipediaApiService
) {
    suspend fun getCityInfo(cityName: String): WikipediaResponse {
        return api.getCitySummary(cityName)
    }
}
