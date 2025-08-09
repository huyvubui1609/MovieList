package com.example.movielist.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movielist.data.local.entities.MovieDetailEntity
import com.example.movielist.data.local.entities.TrendingCacheEntity

@Dao
interface MovieDao {
    @Query("SELECT * FROM trending_cache WHERE page = :page")
    suspend fun getTrendingCache(page: Int): TrendingCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrendingCache(cache: TrendingCacheEntity)

    @Query("SELECT * FROM movie_details WHERE id = :id")
    suspend fun getMovieDetail(id: Int): MovieDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetail(detail: MovieDetailEntity)
}
