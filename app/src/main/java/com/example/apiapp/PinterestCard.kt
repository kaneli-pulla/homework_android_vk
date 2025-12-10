package com.example.apiapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

@Composable
fun PinterestCard(
    gifUrl: String,
    index: Int,
    onGifClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val aspectRatio = remember { mutableStateOf(1f) }
    val imageLoader = ImageCacheManager.getImageLoader(context)

    LaunchedEffect(gifUrl) {
        aspectRatio.value = withContext(Dispatchers.IO) {
            try {
                getGifAspectRatio(gifUrl)
            } catch (e: Exception) {
                e.printStackTrace()
                1f
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio.value)
            .clickable {
                /*Toast.makeText(
                    context,
                    String.format(context.getString(R.string.gif_item_text), index + 1),
                    Toast.LENGTH_SHORT
                ).show()*/
                onGifClick(gifUrl)
            },
        shape = RoundedCornerShape(
            dimensionResource(id = R.dimen.pinterest_card_corner_radius)
        ),
        elevation = CardDefaults.cardElevation(
            dimensionResource(id = R.dimen.pinterest_card_elevation)
        )
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(gifUrl)
                .crossfade(true)
                .build(),
            contentDescription = String.format(context.getString(R.string.gif_item_text), index + 1),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            imageLoader = imageLoader
        )
    }
}

private fun getGifAspectRatio(gifUrl: String): Float {
    return try {
        val url = URL(gifUrl)
        val connection = url.openConnection().apply {
            connectTimeout = R.integer.gif_connection_timeout
            readTimeout = R.integer.gif_connection_timeout
        }

        connection.getInputStream().use { input ->
            val header = ByteArray(10)
            val read = input.read(header)
            if (read >= 10) {
                val width = ((header[7].toInt() and 0xFF) shl 8) or (header[6].toInt() and 0xFF)
                val height = ((header[9].toInt() and 0xFF) shl 8) or (header[8].toInt() and 0xFF)
                if (width > 0 && height > 0) {
                    width.toFloat() / height.toFloat()
                } else {
                    1f
                }
            } else {
                1f
            }
        }
    } catch (e: Exception) {
        1f
    }
}