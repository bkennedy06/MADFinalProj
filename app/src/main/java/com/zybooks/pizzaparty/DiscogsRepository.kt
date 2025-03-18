package com.zybooks.pizzaparty

import android.content.Context
import android.util.Log

class DiscogsRepository(private val context: Context) {
    private val apiKey = context.getString(R.string.discogs_api_key)
    private val apiSecret = context.getString(R.string.discogs_api_secret)

    suspend fun searchAlbums(query: String): List<DiscogsAlbumResult> {
        return try {
            val response = RetrofitInstance.api.searchAlbums(query, apiKey = apiKey, apiSecret = apiSecret)
            Log.d("APIResponse", "Full Response: $response")
            response.results
        } catch (e: Exception) {
            //Log.e("APIError", "Failed to fetch albums: ${e.message}")
            emptyList()
        }
    }

}
