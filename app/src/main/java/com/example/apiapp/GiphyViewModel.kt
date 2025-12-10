package com.example.apiapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GiphyViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = GiphyRepository(application)

    private val _gifUrls = MutableStateFlow<List<String>>(emptyList())
    val gifUrls: StateFlow<List<String>> = _gifUrls.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _loadMoreError = MutableStateFlow<String?>(null)
    val loadMoreError: StateFlow<String?> = _loadMoreError.asStateFlow()

    private var currentOffset = 0
    private var currentQuery: String? = null
    private var hasMore = true
    private var totalCount = 0

    private fun loadPage(
        reset: Boolean,
        errorPrefix: String,
        loader: suspend (limit: Int, offset: Int) -> Result<GiphyRepository.GifsResult>
    ) {
        if (reset) {
            currentOffset = 0
            _gifUrls.value = emptyList()
            hasMore = true
            _error.value = null
        }

        viewModelScope.launch {
            if (reset) _isLoading.value = true else _isLoadingMore.value = true
            _loadMoreError.value = null

            val result = loader(getApplication<Application>().resources.getInteger(R.integer.page_size), currentOffset)

            if (result.isSuccess) {
                val gifsResult = result.getOrThrow()
                val newUrls = gifsResult.urls
                totalCount = gifsResult.totalCount

                if (newUrls.isEmpty()) {
                    hasMore = false
                } else {
                    val newOffset = currentOffset + newUrls.size
                    hasMore = newOffset < totalCount || newUrls.size >= getApplication<Application>().resources.getInteger(R.integer.page_size)
                }

                _gifUrls.value = if (reset) newUrls else _gifUrls.value + newUrls
                currentOffset += newUrls.size
                _error.value = null
            } else {
                val errorMsg =
                    "$errorPrefix: ${result.exceptionOrNull()?.message ?: getApplication<Application>().getString(R.string.error_unknown)}"
                if (reset) {
                    _error.value = errorMsg
                } else {
                    _loadMoreError.value = errorMsg
                }
            }

            if (reset) _isLoading.value = false else _isLoadingMore.value = false
        }
    }

    fun loadGifs(reset: Boolean = true) {
        if (reset) currentQuery = null
        loadPage(
            reset = reset,
            errorPrefix = getApplication<Application>().getString(R.string.error_load_prefix),
            loader = { limit, offset -> repository.getTrendingGifs(limit, offset) }
        )
    }

    fun searchGifs(query: String, reset: Boolean = true) {
        if (reset) currentQuery = query
        loadPage(
            reset = reset,
            errorPrefix = getApplication<Application>().getString(R.string.error_search_prefix),
            loader = { limit, offset -> repository.searchGifs(query, limit, offset) }
        )
    }

    fun loadMore() {
        if (_isLoadingMore.value || !hasMore) return

        if (currentQuery != null) {
            searchGifs(currentQuery!!, reset = false)
        } else {
            loadGifs(reset = false)
        }
    }

    fun retryLoad() {
        if (gifUrls.value.isEmpty()) {
            if (currentQuery != null) {
                searchGifs(currentQuery!!, reset = true)
            } else {
                loadGifs(reset = true)
            }
        } else {
            loadMore()
        }
    }
}