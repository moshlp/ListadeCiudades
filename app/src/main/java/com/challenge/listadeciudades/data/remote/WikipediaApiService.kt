package com.challenge.listadeciudades.data.remote

import com.challenge.listadeciudades.data.remote.model.WikipediaResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface WikipediaApiService {

    @GET("page/summary/{city}")
    suspend fun getCitySummary(
        @Path("city") city: String
    ): WikipediaResponse
}
