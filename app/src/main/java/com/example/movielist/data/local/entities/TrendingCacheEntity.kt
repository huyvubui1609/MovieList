package com.example.movielist.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trending_cache")
data class TrendingCacheEntity(
    @PrimaryKey val page: Int = 1,
    val moviesJson: String,
    val lastFetched: Long
)
