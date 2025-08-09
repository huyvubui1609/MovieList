package com.example.movielist.di

import com.example.movielist.domain.model.Movie
import com.example.movielist.domain.model.MovieDetail
import com.example.movielist.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
abstract class TestAppModule {

    @Binds
    abstract fun bindMovieRepository(impl: FakeMovieRepository): MovieRepository

    companion object {
        @Provides
        @Singleton
        fun provideFakeMovieRepository(): FakeMovieRepository = FakeMovieRepository()
    }
}

class FakeMovieRepository : MovieRepository {

    private val fakeMovies = listOf(
        Movie(
            id = 1,
            title = "Test Movie 1",
            releaseDate = "2023-01-01",
            posterPath = "/test1.jpg",
            backdropPath = "/test1_backdrop.jpg",
            voteAverage = 8.5,
        ),
        Movie(
            id = 2,
            title = "Test Movie 2",
            releaseDate = "2023-02-01",
            posterPath = "/test2.jpg",
            backdropPath = "/test2_backdrop.jpg",
            voteAverage = 7.8,
        )
    )

    private val fakeMovieDetail = MovieDetail(
        id = 1,
        title = "Test Movie 1",
        overview = "This is a detailed test movie overview",
        releaseDate = "2023-01-01",
        runtime = 120,
        homepage = "https://example.com",
        posterPath = "/test1.jpg",
        backdropPath = "/test1_backdrop.jpg",
        voteAverage = 8.5
    )

    override suspend fun getTrendingMovies(page: Int): List<Movie> {
        return fakeMovies
    }

    override suspend fun searchMovies(query: String, page: Int): List<Movie> {
        return fakeMovies.filter { it.title.contains(query, ignoreCase = true) }
    }

    override suspend fun getMovieDetail(id: Int): MovieDetail {
        return fakeMovieDetail.copy(id = id)
    }
}
