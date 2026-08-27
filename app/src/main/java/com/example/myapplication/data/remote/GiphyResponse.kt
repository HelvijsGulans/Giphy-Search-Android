package com.example.myapplication.data.remote

import com.google.gson.annotations.SerializedName

data class GiphyResponse(
    val data: List<GifDto>
)

data class GifDto(
    val id: String,
    val title: String,
    val images: ImagesDto,

)

data class ImagesDto(
    val original: GifImageDto,
    @SerializedName("fixed_width")
    val fixedWidth: GifImageDto
)

data class GifImageDto(
    val url: String
)

data class SingleGifResponse (
    val data: GifDto
)