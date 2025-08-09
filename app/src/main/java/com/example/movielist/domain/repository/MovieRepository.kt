package com.example.movielist.domain.repository

import com.example.movielist.domain.model.Movie
import com.example.movielist.domain.model.MovieDetail

// Keep repository interface platform-agnostic to move into KMM shared module easily.
interface MovieRepository {
    suspend fun getTrendingMovies(page: Int = 1): List<Movie>
    suspend fun searchMovies(query: String, page: Int = 1): List<Movie>
    suspend fun getMovieDetail(id: Int): MovieDetail
}
