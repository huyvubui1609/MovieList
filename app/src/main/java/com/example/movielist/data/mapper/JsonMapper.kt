package com.example.movielist.data.mapper

import com.example.movielist.data.network.dto.TmdbMovieDto
import com.example.movielist.data.network.dto.TmdbMovieDetailResponse
import com.example.movielist.domain.model.Movie
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

class JsonMapper @Inject constructor(
    private val moshi: Moshi,
    private val dtoMapper: DtoMapper
) {
    private val movieDtoListAdapter: JsonAdapter<List<TmdbMovieDto>> =
        moshi.adapter(Types.newParameterizedType(List::class.java, TmdbMovieDto::class.java))

    private val movieDetailAdapter: JsonAdapter<TmdbMovieDetailResponse> =
        moshi.adapter(TmdbMovieDetailResponse::class.java)

    fun toJson(movieDtos: List<TmdbMovieDto>): String {
        return movieDtoListAdapter.toJson(movieDtos)
    }

    fun fromMoviesJson(json: String): List<Movie> {
        val dtos = movieDtoListAdapter.fromJson(json) ?: emptyList()
        return dtos.map { dtoMapper.toDomain(it) }
    }

    fun toJsonDetail(movieDetailDto: TmdbMovieDetailResponse): String {
        return movieDetailAdapter.toJson(movieDetailDto)
    }

    fun fromMovieDetailJson(json: String): com.example.movielist.domain.model.MovieDetail {
        val dto = movieDetailAdapter.fromJson(json)
            ?: throw IllegalArgumentException("Invalid movie detail JSON")
        return dtoMapper.toDomainDetail(dto)
    }
}
