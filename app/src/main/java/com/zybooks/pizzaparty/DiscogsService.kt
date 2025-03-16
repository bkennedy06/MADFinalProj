package com.zybooks.pizzaparty

import retrofit2.http.GET
import retrofit2.http.Query

interface DiscogsService {
    @GET("database/search")
    suspend fun searchAlbums(
        @Query("q") query: String,
        @Query("type") type: String = "release",
        @Query("key") apiKey: String,
        @Query("secret") apiSecret: String
    ): DiscogsSearchResponse
}
