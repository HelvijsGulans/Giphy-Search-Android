package com.example.myapplication.data.remote

import retrofit2.http.Path
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyApi {

    @GET("v1/gifs/search")
    suspend fun searchGifs(
        @Query("api_key") apiKey: String,
        @Query("q") query: String,
        @Query("limit") limit: Int = 25,
        @Query("offset") offset: Int = 0
    ): GiphyResponse

    @GET("v1/gifs/{id}")
    suspend fun searchGifById(
        @Path("id") id: String,
        @Query("api_key") apiKey: String
    ): SingleGifResponse
}