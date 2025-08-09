package com.example.movielist.domain.usecase

import com.example.movielist.domain.model.MovieDetail
import com.example.movielist.domain.repository.MovieRepository
import javax.inject.Inject

// Keep use case classes platform-agnostic to move into KMM shared module easily.
class GetMovieDetailUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(id: Int): MovieDetail {
        return repository.getMovieDetail(id)
    }
}
