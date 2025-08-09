package com.example.movielist.util

// Keep Logger interface platform-agnostic to move into KMM shared module easily.
interface Logger {
    fun d(tag: String, message: String)
}
