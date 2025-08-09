package com.example.movielist.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_cache")
data class SearchCacheEntity(
    @PrimaryKey val searchKey: String, // combination of query + page
    val query: String,
    val page: Int,
    val moviesJson: String,
    val lastFetched: Long
)
