package com.example.apiapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyApiService {

    companion object { // должны быть compile-time константами
        private const val TRENDING_ENDPOINT = "gifs/trending"
        private const val SEARCH_ENDPOINT = "gifs/search"

        private const val DEFAULT_LIMIT = 25
        private const val DEFAULT_OFFSET = 0
        private const val DEFAULT_RATING = "g"
        private const val DEFAULT_LANG = "en"
    }

    @GET(TRENDING_ENDPOINT)
    suspend fun getTrendingGifs(
        @Query("api_key") apiKey: String,
        @Query("limit") limit: Int = DEFAULT_LIMIT,
        @Query("offset") offset: Int = DEFAULT_OFFSET,
        @Query("rating") rating: String = DEFAULT_RATING
    ): Response<GiphyResponse>

    @GET(SEARCH_ENDPOINT)
    suspend fun searchGifs(
        @Query("api_key") apiKey: String,
        @Query("q") query: String,
        @Query("limit") limit: Int = DEFAULT_LIMIT,
        @Query("offset") offset: Int = DEFAULT_OFFSET,
        @Query("rating") rating: String = DEFAULT_RATING,
        @Query("lang") lang: String = DEFAULT_LANG
    ): Response<GiphyResponse>
}