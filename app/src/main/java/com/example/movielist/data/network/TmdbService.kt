package com.example.movielist.data.network

import com.example.movielist.data.network.dto.TmdbMovieDetailResponse
import com.example.movielist.data.network.dto.TmdbMoviesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbService {
    @GET("trending/movie/day")
    suspend fun getTrending(@Query("page") page: Int = 1): TmdbMoviesResponse

    @GET("search/movie")
    suspend fun searchMovies(@Query("query") query: String, @Query("page") page: Int = 1): TmdbMoviesResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") id: Int): TmdbMovieDetailResponse
}
