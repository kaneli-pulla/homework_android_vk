package com.example.apiapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GiphyScreen(viewModel: GiphyViewModel = viewModel()) {
    val gifUrls = viewModel.gifUrls.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val isLoadingMore = viewModel.isLoadingMore.collectAsState().value
    val error = viewModel.error.collectAsState().value
    val loadMoreError = viewModel.loadMoreError.collectAsState().value

    var selectedGifUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (gifUrls.isEmpty() && error == null) {
            viewModel.loadGifs()
        }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                (isLoading && gifUrls.isEmpty()) -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                (error != null && gifUrls.isEmpty()) -> {
                    ErrorScreen(
                        errorMessage = error,
                        onRetry = { viewModel.retryLoad() },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                gifUrls.isNotEmpty() -> {
                    PinterestGrid(
                        gifUrls = gifUrls,
                        isLoadingMore = isLoadingMore,
                        loadMoreError = loadMoreError,
                        onLoadMore = { viewModel.loadMore() },
                        onRetryLoadMore = { viewModel.retryLoad() },
                        onGifClick = { url -> selectedGifUrl = url },
                        modifier = Modifier.fillMaxSize(),
                        columns = 2
                    )
                }

                else -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            if (selectedGifUrl != null) {
                DetailScreen(
                    gifUrl = selectedGifUrl!!,
                    onClose = {
                        selectedGifUrl = null
                    }
                )
            }
        }
    }
}