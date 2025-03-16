package com.zybooks.pizzaparty

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "RecordHolderApp/1.0 +http://RecordHolderExampleBcIdontHaveasebsite.com") // Make this unique
                .build()
            chain.proceed(request)
        }
        .build()

    val api: DiscogsService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.discogs.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // Add client with User-Agent
            .build()
            .create(DiscogsService::class.java)
    }
}

