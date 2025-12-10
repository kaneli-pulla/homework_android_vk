package com.example.apiapp

import android.content.Context
import android.os.Build
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy

object ImageCacheManager {
    private var imageLoader: ImageLoader? = null

    fun getImageLoader(context: Context): ImageLoader {
        return imageLoader ?: ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(context.resources.getInteger(R.integer.memory_cache_max_size_percent) / 100.0)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(context.resources.getInteger(R.integer.disk_cache_max_size_percent) / 100.0)
                    .build()
            }
            .respectCacheHeaders(false)
            .build().also { imageLoader = it }
    }
}