package com.example.myapplication.data.repository

import com.example.myapplication.BuildConfig
import com.example.myapplication.data.remote.GifDto
import com.example.myapplication.data.remote.GiphyApi

class GifRepository(
    private val api: GiphyApi
) {

    suspend fun searchGifs(
        query: String,
        offset: Int = 0
    ): List<GifDto> {

        val response = api.searchGifs(
            apiKey = BuildConfig.GIPHY_API_KEY,
            query = query,
            offset = offset
        )
        return response.data

    }

    suspend fun getGifById(id: String): GifDto {
        val singleGif = api.searchGifById(
            apiKey = BuildConfig.GIPHY_API_KEY,
            id = id
        )
        return singleGif.data
    }
}