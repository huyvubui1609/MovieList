package com.example.movielist.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.movielist.data.local.AppDatabase
import com.example.movielist.data.local.MovieDao
import com.example.movielist.data.repository.MovieRepositoryImpl
import com.example.movielist.domain.repository.MovieRepository
import com.example.movielist.util.Logger
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindMovieRepository(impl: MovieRepositoryImpl): MovieRepository

    companion object {
        @Provides
        @Singleton
        fun provideMoshi(): Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                AppDatabase.DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build()

        @Provides
        fun provideMovieDao(database: AppDatabase): MovieDao = database.movieDao()

        @Provides
        @Singleton
        fun provideLogger(): Logger = object : Logger {
            override fun d(tag: String, message: String) {
                Log.d(tag, message)
                println("[$tag] $message") // Also print to stdout for KMM compatibility
            }
        }
    }
}
