package com.example.movielist.presentation.moviedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movielist.domain.model.MovieDetail
import com.example.movielist.domain.usecase.GetMovieDetailUseCase
import com.example.movielist.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
) : ViewModel() {

    private val _detailState = MutableStateFlow<UiState<MovieDetail>>(UiState.Loading)
    val detailState: StateFlow<UiState<MovieDetail>> = _detailState.asStateFlow()

    private val _showOfflineIndicator = MutableStateFlow(false)
    val showOfflineIndicator: StateFlow<Boolean> = _showOfflineIndicator.asStateFlow()

    fun loadMovieDetail(id: Int) {
        viewModelScope.launch {
            _detailState.value = UiState.Loading
            _showOfflineIndicator.value = false

            try {
                val movieDetail = getMovieDetailUseCase(id)
                _detailState.value = UiState.Success(movieDetail)
            } catch (e: Exception) {
                _showOfflineIndicator.value = true
                _detailState.value = UiState.Error(getNetworkErrorMessage(e))
            }
        }
    }

    private fun getNetworkErrorMessage(exception: Exception): String {
        return when {
            exception.message?.contains("Unable to resolve host") == true ||
            exception.message?.contains("No address associated with hostname") == true ->
                "No internet connection. Showing cached details if available."
            exception.message?.contains("timeout") == true ->
                "Connection timeout. Please check your internet connection."
            else -> "Network error occurred. ${exception.message ?: "Please try again."}"
        }
    }

    fun retry(id: Int) {
        loadMovieDetail(id)
    }
}
