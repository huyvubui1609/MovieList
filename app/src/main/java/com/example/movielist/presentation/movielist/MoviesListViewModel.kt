package com.example.movielist.presentation.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movielist.domain.model.Movie
import com.example.movielist.domain.usecase.GetTrendingMoviesUseCase
import com.example.movielist.domain.usecase.SearchMoviesUseCase
import com.example.movielist.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class MoviesListViewModel @Inject constructor(
    private val getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

    private val _moviesState = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val moviesState: StateFlow<UiState<List<Movie>>> = _moviesState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showOfflineIndicator = MutableStateFlow(false)
    val showOfflineIndicator: StateFlow<Boolean> = _showOfflineIndicator.asStateFlow()

    init {
        // Debounce search queries and trigger appropriate loading
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    if (query.isBlank()) {
                        flowOf(SearchAction.LoadTrending)
                    } else {
                        flowOf(SearchAction.Search(query))
                    }
                }
                .collect { action ->
                    when (action) {
                        is SearchAction.LoadTrending -> loadTrendingMovies()
                        is SearchAction.Search -> searchMovies(action.query)
                    }
                }
        }

        // Load trending movies initially
        loadTrendingMovies()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private fun loadTrendingMovies() {
        viewModelScope.launch {
            _moviesState.value = UiState.Loading
            _showOfflineIndicator.value = false

            try {
                val movies = getTrendingMoviesUseCase()
                if (movies.isEmpty()) {
                    _moviesState.value = UiState.Empty
                } else {
                    _moviesState.value = UiState.Success(movies)
                }
            } catch (e: Exception) {
                _moviesState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun searchMovies(query: String) {
        viewModelScope.launch {
            _moviesState.value = UiState.Loading
            _showOfflineIndicator.value = false

            try {
                val movies = searchMoviesUseCase(query)
                if (movies.isEmpty()) {
                    _moviesState.value = UiState.Empty
                } else {
                    _moviesState.value = UiState.Success(movies)
                }
            } catch (e: Exception) {
                _moviesState.value = UiState.Error(e.message ?: "Search failed")
            }
        }
    }

    fun retry() {
        if (_searchQuery.value.isBlank()) {
            loadTrendingMovies()
        } else {
            searchMovies(_searchQuery.value)
        }
    }

    private sealed class SearchAction {
        object LoadTrending : SearchAction()
        data class Search(val query: String) : SearchAction()
    }
}
