package com.example.apiapp

import android.content.Context
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit

class GiphyRepository(
    private val context: Context
) {
    private val apiService = RetrofitInstance.api
    private val apiKey = RetrofitInstance.getApiKey()

    private val trendingCache = mutableMapOf<String, CacheRecord<List<String>>>()
    private val searchCache = mutableMapOf<String, CacheRecord<List<String>>>()
    private val cacheMutex = Mutex()
    private val cacheDuration = TimeUnit.MINUTES.toMillis(
        context.resources.getInteger(R.integer.cache_duration).toLong()
    )

    data class CacheRecord<T>(
        val data: T,
        val timestamp: Long,
        val offset: Int,
        val totalCount: Int
    )

    data class GifsResult(
        val urls: List<String>,
        val totalCount: Int,
        val offset: Int
    )

    suspend fun getTrendingGifs(
        limit: Int = context.resources.getInteger(R.integer.gifs_limit),
        offset: Int = 0
    ): Result<GifsResult> {
        val cacheKey = String.format(
            context.getString(R.string.trending_cache_key_format),
            limit,
            offset
        )

        return cacheMutex.withLock {
            val cached = trendingCache[cacheKey]

            if (cached != null && (System.currentTimeMillis() - cached.timestamp) < cacheDuration) {
                Result.success(
                    GifsResult(
                        urls = cached.data,
                        totalCount = cached.totalCount,
                        offset = cached.offset
                    )
                )
            } else {
                try {
                    val response = apiService.getTrendingGifs(
                        apiKey = apiKey,
                        limit = limit,
                        offset = offset
                    )

                    if (response.isSuccessful) {
                        val giphyResponse = response.body()
                        val gifUrls = giphyResponse?.data?.map { it.images.fixed_height.url } ?: emptyList()
                        val totalCount = giphyResponse?.pagination?.total_count ?: 0

                        trendingCache[cacheKey] = CacheRecord(
                            data = gifUrls,
                            timestamp = System.currentTimeMillis(),
                            offset = offset,
                            totalCount = totalCount
                        )

                        Result.success(
                            GifsResult(
                                urls = gifUrls,
                                totalCount = totalCount,
                                offset = offset
                            )
                        )
                    } else {
                        Result.failure(Exception(
                            context.getString(R.string.error_api, response.code())
                        ))
                    }
                } catch (e: Exception) {
                    cached?.let {
                        Result.success(
                            GifsResult(
                                urls = it.data,
                                totalCount = it.totalCount,
                                offset = it.offset
                            )
                        )
                    } ?: Result.failure(e)
                }
            }
        }
    }

    suspend fun searchGifs(
        query: String,
        limit: Int = context.resources.getInteger(R.integer.gifs_limit),
        offset: Int = 0
    ): Result<GifsResult> {
        val cacheKey = String.format(
            context.getString(R.string.search_cache_key_format),
            query,
            limit,
            offset
        )

        return cacheMutex.withLock {
            val cached = searchCache[cacheKey]

            if (cached != null && (System.currentTimeMillis() - cached.timestamp) < cacheDuration) {
                Result.success(
                    GifsResult(
                        urls = cached.data,
                        totalCount = cached.totalCount,
                        offset = cached.offset
                    )
                )
            } else {
                try {
                    val response = apiService.searchGifs(
                        apiKey = apiKey,
                        query = query,
                        limit = limit,
                        offset = offset
                    )

                    if (response.isSuccessful) {
                        val giphyResponse = response.body()
                        val gifUrls = giphyResponse?.data?.map { it.images.fixed_height.url } ?: emptyList()
                        val totalCount = giphyResponse?.pagination?.total_count ?: 0

                        searchCache[cacheKey] = CacheRecord(
                            data = gifUrls,
                            timestamp = System.currentTimeMillis(),
                            offset = offset,
                            totalCount = totalCount
                        )

                        Result.success(
                            GifsResult(
                                urls = gifUrls,
                                totalCount = totalCount,
                                offset = offset
                            )
                        )
                    } else {
                        Result.failure(Exception(
                            context.getString(R.string.error_api, response.code())
                        ))
                    }
                } catch (e: Exception) {
                    cached?.let {
                        Result.success(
                            GifsResult(
                                urls = it.data,
                                totalCount = it.totalCount,
                                offset = it.offset
                            )
                        )
                    } ?: Result.failure(e)
                }
            }
        }
    }

    fun clearCache() {
        trendingCache.clear()
        searchCache.clear()
    }
}