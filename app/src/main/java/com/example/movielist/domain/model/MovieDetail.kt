package com.example.movielist.domain.model

// Keep domain classes platform-agnostic to move into KMM shared module easily.
data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String?,
    val releaseDate: String?,
    val runtime: Int?,
    val homepage: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double
)
