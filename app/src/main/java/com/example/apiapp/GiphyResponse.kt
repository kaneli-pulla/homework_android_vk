package com.example.apiapp;


data class GiphyResponse(
        val data: List<GifData>,
        val pagination: Pagination,
        val meta: Meta
)

data class GifData(
        val id: String,
        val images: Images,
        val title: String
)

data class Images(
        val fixed_height: ImageInfo,
        val fixed_height_downsampled: ImageInfo,
        val original: ImageInfo
)

data class ImageInfo(
        val url: String,
        val width: String,
        val height: String,
        val size: String? = null
)

data class Pagination(
        val total_count: Int,
        val count: Int,
        val offset: Int
)

data class Meta(
        val status: Int,
        val msg: String,
        val response_id: String
)