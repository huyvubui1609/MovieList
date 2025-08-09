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
    private val getMovieDetailUseCase: GetMovieDetailUseCase
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
                // Note: In a real implementation, we'd detect if this came from cache
                // For now, we assume cached data doesn't throw exceptions
            } catch (e: Exception) {
                _detailState.value = UiState.Error(e.message ?: "Failed to load movie details")
            }
        }
    }

    fun retry(id: Int) {
        loadMovieDetail(id)
    }
}
