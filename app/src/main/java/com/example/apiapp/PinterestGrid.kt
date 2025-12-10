package com.example.apiapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource

@Suppress("NonSkippableComposable")
@Composable
fun PinterestGrid(
    gifUrls: List<String>,
    isLoadingMore: Boolean = false,
    loadMoreError: String? = null,
    onLoadMore: () -> Unit = {},
    onRetryLoadMore: () -> Unit = {},
    onGifClick: (String) -> Unit = { _ -> },
    modifier: Modifier = Modifier,
    columns: Int = LocalContext.current.resources.getInteger(R.integer.pinterest_grid_columns)
) {
    val context = LocalContext.current
    val prefetchThreshold = remember { context.resources.getInteger(R.integer.pinterest_grid_prefetch_threshold) }
    val gridSpacing = dimensionResource(id = R.dimen.pinterest_grid_spacing)
    val itemPadding = dimensionResource(id = R.dimen.pinterest_grid_item_padding)

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        verticalItemSpacing = gridSpacing,
        horizontalArrangement = Arrangement.spacedBy(gridSpacing),
        contentPadding = PaddingValues(gridSpacing)
    ) {
        itemsIndexed(gifUrls) { index, gifUrl ->
            if (index >= gifUrls.size - prefetchThreshold && !isLoadingMore && loadMoreError == null) {
                LaunchedEffect(key1 = index) {
                    onLoadMore()
                }
            }

            PinterestCard(
                gifUrl = gifUrl,
                index = index,
                onGifClick = onGifClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(itemPadding)
            )
        }

        if (isLoadingMore) {
            item(span = StaggeredGridItemSpan.FullLine) {
                LoadingIndicator()
            }
        }

        if (loadMoreError != null && !isLoadingMore) {
            item(span = StaggeredGridItemSpan.FullLine) {
                RetryButton(
                    onClick = onRetryLoadMore,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}