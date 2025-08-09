package com.example.movielist.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.movielist.data.local.entities.MovieDetailEntity
import com.example.movielist.data.local.entities.SearchCacheEntity
import com.example.movielist.data.local.entities.TrendingCacheEntity
import android.content.Context

@Database(
    entities = [TrendingCacheEntity::class, MovieDetailEntity::class, SearchCacheEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        const val DATABASE_NAME = "movie_database"
    }
}
