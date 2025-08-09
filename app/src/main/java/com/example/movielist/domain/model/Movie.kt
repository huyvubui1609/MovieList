package com.example.movielist.domain.model

// Keep domain classes platform-agnostic to move into KMM shared module easily.
data class Movie(
    val id: Int,
    val title: String,
    val releaseDate: String?,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double
) {
    fun year(): String? = releaseDate?.takeIf { it.length >= 4 }?.substring(0, 4)
}
