package com.example.movielist.data.repository

import com.example.movielist.data.local.MovieDao
import com.example.movielist.data.local.entities.MovieDetailEntity
import com.example.movielist.data.local.entities.SearchCacheEntity
import com.example.movielist.data.local.entities.TrendingCacheEntity
import com.example.movielist.data.mapper.DtoMapper
import com.example.movielist.data.mapper.JsonMapper
import com.example.movielist.data.network.TmdbService
import com.example.movielist.data.network.dto.TmdbMovieDetailResponse
import com.example.movielist.data.network.dto.TmdbMovieDto
import com.example.movielist.data.network.dto.TmdbMoviesResponse
import com.example.movielist.domain.model.Movie
import com.example.movielist.domain.model.MovieDetail
import com.example.movielist.util.Logger
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.io.IOException

class MovieRepositoryImplTest {

    private lateinit var tmdbService: TmdbService
    private lateinit var dao: MovieDao
    private lateinit var dtoMapper: DtoMapper
    private lateinit var jsonMapper: JsonMapper
    private lateinit var logger: Logger
    private lateinit var repository: MovieRepositoryImpl

    // Test data
    private val testMovieDto = TmdbMovieDto(
        id = 1,
        title = "Test Movie",
        releaseDate = "2023-01-01",
        posterPath = "/test.jpg",
        backdropPath = "/test_backdrop.jpg",
        voteAverage = 8.5,
        overview = "Test overview"
    )

    private val testMovie = Movie(
        id = 1,
        title = "Test Movie",
        releaseDate = "2023-01-01",
        posterPath = "/test.jpg",
        backdropPath = "/test_backdrop.jpg",
        voteAverage = 8.5
    )

    private val testMovieDetailDto = TmdbMovieDetailResponse(
        id = 1,
        title = "Test Movie",
        releaseDate = "2023-01-01",
        runtime = 120,
        homepage = "https://example.com",
        posterPath = "/test.jpg",
        backdropPath = "/test_backdrop.jpg",
        voteAverage = 8.5,
        overview = "Test overview"
    )

    private val testMovieDetail = MovieDetail(
        id = 1,
        title = "Test Movie",
        releaseDate = "2023-01-01",
        runtime = 120,
        homepage = "https://example.com",
        posterPath = "/test.jpg",
        backdropPath = "/test_backdrop.jpg",
        voteAverage = 8.5,
        overview = "Test overview"
    )

    private val testTrendingResponse = TmdbMoviesResponse(
        page = 1,
        results = listOf(testMovieDto),
        totalPages = 1,
        totalResults = 1
    )

    @Before
    fun setup() {
        // Create mocks
        tmdbService = mockk()
        dao = mockk()
        dtoMapper = mockk()
        jsonMapper = mockk()
        logger = mockk(relaxed = true)

        repository = MovieRepositoryImpl(tmdbService, dao, dtoMapper, jsonMapper, logger)

        // Mock static CacheValidator
        mockkObject(com.example.movielist.util.CacheValidator)

        // Default mock behaviors
        every { dtoMapper.toDomain(testMovieDto) } returns testMovie
        every { dtoMapper.toDomainDetail(testMovieDetailDto) } returns testMovieDetail
        every { jsonMapper.toJson(any<List<TmdbMovieDto>>()) } returns "mock_json"
        every { jsonMapper.toJsonDetail(testMovieDetailDto) } returns "mock_detail_json"
        every { jsonMapper.fromMoviesJson("mock_json") } returns listOf(testMovie)
        every { jsonMapper.fromMovieDetailJson("mock_detail_json") } returns testMovieDetail
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // getTrendingMovies Tests
    @Test
    fun `getTrendingMovies returns cached data when cache is valid`() = runTest {
        // Given
        val page = 1
        val cachedEntity = TrendingCacheEntity(
            page = page,
            moviesJson = "mock_json",
            lastFetched = System.currentTimeMillis()
        )
        coEvery { dao.getTrendingCache(page) } returns cachedEntity
        every { com.example.movielist.util.CacheValidator.isValid(any()) } returns true

        // When
        val result = repository.getTrendingMovies(page)

        // Then
        assertEquals(listOf(testMovie), result)
        verify { jsonMapper.fromMoviesJson("mock_json") }
        coVerify(exactly = 0) { tmdbService.getTrending(any()) }
    }

    @Test
    fun `getTrendingMovies fetches from network when no cache exists`() = runTest {
        // Given
        val page = 1
        coEvery { dao.getTrendingCache(page) } returns null
        coEvery { tmdbService.getTrending(page) } returns testTrendingResponse
        coEvery { dao.insertTrendingCache(any()) } returns Unit

        // When
        val result = repository.getTrendingMovies(page)

        // Then
        assertEquals(listOf(testMovie), result)
        coVerify { tmdbService.getTrending(page) }
        coVerify { dao.insertTrendingCache(any()) }
    }

    @Test
    fun `getTrendingMovies returns stale cache when network fails`() = runTest {
        // Given
        val page = 1
        val cachedEntity = TrendingCacheEntity(
            page = page,
            moviesJson = "mock_json",
            lastFetched = System.currentTimeMillis() - 100000
        )
        coEvery { dao.getTrendingCache(page) } returns cachedEntity
        every { com.example.movielist.util.CacheValidator.isValid(any()) } returns false
        coEvery { tmdbService.getTrending(page) } throws IOException("Network error")

        // When
        val result = repository.getTrendingMovies(page)

        // Then
        assertEquals(listOf(testMovie), result)
        coVerify { tmdbService.getTrending(page) }
    }

    @Test
    fun `searchMovies returns cached data when cache is valid`() = runTest {
        // Given
        val query = "batman"
        val page = 1
        val searchKey = "${query}_$page"
        val cachedEntity = SearchCacheEntity(
            searchKey = searchKey,
            query = query,
            page = page,
            moviesJson = "mock_json",
            lastFetched = System.currentTimeMillis()
        )
        coEvery { dao.getSearchCache(searchKey) } returns cachedEntity
        every { com.example.movielist.util.CacheValidator.isValid(any()) } returns true

        // When
        val result = repository.searchMovies(query, page)

        // Then
        assertEquals(listOf(testMovie), result)
        verify { jsonMapper.fromMoviesJson("mock_json") }
        coVerify(exactly = 0) { tmdbService.searchMovies(any(), any()) }
    }

    @Test
    fun `getMovieDetail returns cached data when available`() = runTest {
        // Given
        val movieId = 1
        val cachedEntity = MovieDetailEntity(
            id = movieId,
            json = "mock_detail_json",
            lastFetched = System.currentTimeMillis()
        )
        coEvery { dao.getMovieDetail(movieId) } returns cachedEntity

        // When
        val result = repository.getMovieDetail(movieId)

        // Then
        assertEquals(testMovieDetail, result)
        verify { jsonMapper.fromMovieDetailJson("mock_detail_json") }
        coVerify(exactly = 0) { tmdbService.getMovieDetails(any()) }
    }

    @Test
    fun `getMovieDetail fetches from network when no cache exists`() = runTest {
        // Given
        val movieId = 1
        coEvery { dao.getMovieDetail(movieId) } returns null
        coEvery { tmdbService.getMovieDetails(movieId) } returns testMovieDetailDto
        coEvery { dao.insertMovieDetail(any()) } returns Unit

        // When
        val result = repository.getMovieDetail(movieId)

        // Then
        assertEquals(testMovieDetail, result)
        coVerify { tmdbService.getMovieDetails(movieId) }
        coVerify { dao.insertMovieDetail(any()) }
    }
}
