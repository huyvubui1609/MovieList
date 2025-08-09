package com.example.movielist.domain.usecase

import com.example.movielist.domain.model.Movie
import com.example.movielist.domain.repository.MovieRepository
import javax.inject.Inject

// Keep use case classes platform-agnostic to move into KMM shared module easily.
class GetTrendingMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(page: Int = 1): List<Movie> {
        return repository.getTrendingMovies(page)
    }
}
