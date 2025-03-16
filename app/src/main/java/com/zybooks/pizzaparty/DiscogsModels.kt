package com.zybooks.pizzaparty

data class DiscogsSearchResponse(
    val results: List<DiscogsAlbumResult>
)

data class DiscogsAlbumResult(
    val title: String?,
    val year: String?,
    val genre: List<String>?,
    val cover_image: String?,
    val format: List<String>?,
    val label: List<String>?,
    val country: String?,
    val lowest_price: Float? // If available
)
