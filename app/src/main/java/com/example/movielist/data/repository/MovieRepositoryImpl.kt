package com.example.movielist.data.repository

import com.example.movielist.data.local.MovieDao
import com.example.movielist.data.local.entities.MovieDetailEntity
import com.example.movielist.data.local.entities.TrendingCacheEntity
import com.example.movielist.data.mapper.DtoMapper
import com.example.movielist.data.mapper.JsonMapper
import com.example.movielist.data.network.TmdbService
import com.example.movielist.domain.model.Movie
import com.example.movielist.domain.model.MovieDetail
import com.example.movielist.domain.repository.MovieRepository
import com.example.movielist.util.CacheValidator
import com.example.movielist.util.Logger
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val tmdbService: TmdbService,
    private val dao: MovieDao,
    private val dtoMapper: DtoMapper,
    private val jsonMapper: JsonMapper,
    private val logger: Logger
) : MovieRepository {

    override suspend fun getTrendingMovies(page: Int): List<Movie> {
        val cached = dao.getTrendingCache(page)
        if (cached != null && CacheValidator.isValid(cached.lastFetched)) {
            logger.d("Repo", "Returning cached trending page=$page")
            return jsonMapper.fromMoviesJson(cached.moviesJson)
        }

        try {
            val response = tmdbService.getTrending(page)
            val movies = response.results.map { dtoMapper.toDomain(it) }
            dao.insertTrendingCache(
                TrendingCacheEntity(
                    page = page,
                    moviesJson = jsonMapper.toJson(response.results),
                    lastFetched = System.currentTimeMillis()
                )
            )
            logger.d("Repo", "Fetched trending from network and cached page=$page")
            return movies
        } catch (e: Exception) {
            if (cached != null) {
                logger.d("Repo", "Network failed, returning stale cache page=$page")
                return jsonMapper.fromMoviesJson(cached.moviesJson)
            }
            throw e
        }
    }

    override suspend fun searchMovies(query: String, page: Int): List<Movie> {
        val response = tmdbService.searchMovies(query, page)
        return response.results.map { dtoMapper.toDomain(it) }
    }

    override suspend fun getMovieDetail(id: Int): MovieDetail {
        val cached = dao.getMovieDetail(id)
        if (cached != null) {
            logger.d("Repo", "Returning cached detail id=$id")
            return jsonMapper.fromMovieDetailJson(cached.json)
        }
        val response = tmdbService.getMovieDetails(id)
        val domain = dtoMapper.toDomainDetail(response)
        dao.insertMovieDetail(
            MovieDetailEntity(
                id = id,
                json = jsonMapper.toJsonDetail(response),
                lastFetched = System.currentTimeMillis()
            )
        )
        logger.d("Repo", "Fetched detail from network and cached id=$id")
        return domain
    }
}
