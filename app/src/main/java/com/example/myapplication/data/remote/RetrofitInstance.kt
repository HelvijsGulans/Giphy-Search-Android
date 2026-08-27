package com.example.myapplication.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance{
    private const val BASE_URL = "https://api.giphy.com/"

    val retrofit: Retrofit = getInstance()

    val api: GiphyApi = retrofit.create(GiphyApi::class.java)

    private fun getInstance(): Retrofit {
        val instance = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return instance
    }
}