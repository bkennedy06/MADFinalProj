package com.zybooks.pizzaparty

import android.content.Context

class DiscogsRepository(private val context: Context) {
    private val apiKey = context.getString(R.string.discogs_api_key)
    private val apiSecret = context.getString(R.string.discogs_api_secret)

    suspend fun searchAlbums(query: String): List<DiscogsAlbumResult> {
        return try {
            RetrofitInstance.api.searchAlbums(query, apiKey = apiKey, apiSecret = apiSecret).results
        } catch (e: Exception) {
            emptyList() // Return empty list if error
        }
    }
}
