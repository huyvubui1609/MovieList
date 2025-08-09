package com.example.movielist.data.mapper

import com.example.movielist.data.network.dto.TmdbMovieDto
import com.example.movielist.data.network.dto.TmdbMovieDetailResponse
import com.example.movielist.domain.model.Movie
import com.example.movielist.domain.model.MovieDetail
import javax.inject.Inject

class DtoMapper @Inject constructor() {

    fun toDomain(dto: TmdbMovieDto): Movie {
        return Movie(
            id = dto.id,
            title = dto.title,
            releaseDate = dto.releaseDate,
            posterPath = dto.posterPath,
            backdropPath = dto.backdropPath,
            voteAverage = dto.voteAverage
        )
    }

    fun toDomainDetail(dto: TmdbMovieDetailResponse): MovieDetail {
        return MovieDetail(
            id = dto.id,
            title = dto.title,
            overview = dto.overview,
            releaseDate = dto.releaseDate,
            runtime = dto.runtime,
            homepage = dto.homepage,
            posterPath = dto.posterPath,
            backdropPath = dto.backdropPath,
            voteAverage = dto.voteAverage
        )
    }
}
