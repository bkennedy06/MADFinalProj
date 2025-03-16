package com.zybooks.pizzaparty
import com.google.gson.annotations.SerializedName

data class DiscogsSearchResponse(
    val results: List<DiscogsAlbumResult>
)

data class DiscogsAlbumResult(
    val title: String?,
    val year: String?,
    val genre: List<String>?,
    @SerializedName("cover_image") val coverImage: String?, // Map cover_image to coverImage
    val format: List<String>?,
    val label: List<String>?,
    val country: String?,
    @SerializedName("lowest_price") val lowestPrice: Float?,
    val artist: String?
)
