package com.example.myapplication.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.GifDto
import com.example.myapplication.data.repository.GifRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.HttpException
import java.io.IOException


data class SearchUiState(
    val query: String = "",
    val gifs: List<GifDto> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null
)

class SearchViewModel(
    private val repository: GifRepository

) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())

    val uiState = _uiState.asStateFlow()

    private var currentOffset = 0
    private val pageSize = 25

    suspend fun search(query: String) {
        if (query.isBlank()) {
            _uiState.value = SearchUiState()
            return
        }


            _uiState.value = _uiState.value.copy(
                isLoading = true
            )

            try {

                currentOffset = 0
                val searchResult = repository.searchGifs(query, currentOffset)

                _uiState.value = _uiState.value.copy(
                    gifs = searchResult,
                    isLoading = false,
                    error = null
                )

                currentOffset += pageSize

            } catch (e: CancellationException){
                throw e
            } catch (e: HttpException) {
                val message = when (e.code()) {
                    401 -> "Invalid API key."
                    429 -> "Too many requests. Please try again later."
                    else -> "Server error (${e.code()})."
                }

                _uiState.value = _uiState.value.copy(
                    gifs = emptyList(),
                    isLoading = false,
                    error = message
                )
            } catch (e: IOException) {
                _uiState.value = _uiState.value.copy(
                    gifs = emptyList(),
                    isLoading = false,
                    error = "No internet connection"
                )
            }

            catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    gifs = emptyList(),
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }




    }

    private var searchJob: Job? = null
    fun onTextQueryChanged(textQuery: String) {

        _uiState.value = _uiState.value.copy(
            query = textQuery
        )

        searchJob?.cancel()

        if (textQuery.isBlank()) {
            _uiState.value = _uiState.value.copy(
                query = "",
                gifs = emptyList(),
                isLoading = false,
                error = null
            )
            return
        }

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )

        searchJob = viewModelScope.launch {

            delay(400)
            search(textQuery)
        }
    }

    fun loadMore() {
        val query = _uiState.value.query

        if (_uiState.value.isLoadingMore) {
            return
        }
        if (_uiState.value.query.isBlank()) {
            return
        }
        viewModelScope.launch {

            try {
                _uiState.value = _uiState.value.copy(
                    isLoadingMore = true
                )

                val newGifs = repository.searchGifs(query = query, offset = currentOffset)

                _uiState.value = _uiState.value.copy(
                    gifs = _uiState.value.gifs + newGifs,
                    isLoadingMore = false
                )
                currentOffset += pageSize
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingMore = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

}