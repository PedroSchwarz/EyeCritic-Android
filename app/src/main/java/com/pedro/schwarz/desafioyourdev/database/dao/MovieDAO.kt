package com.pedro.schwarz.desafioyourdev.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.pedro.schwarz.desafioyourdev.model.Movie

@Dao
interface MovieDAO {

    @Query("SELECT * FROM movie ORDER BY publication_date DESC")
    fun fetchMovies(): DataSource.Factory<Int, Movie>

    @Query("SELECT * FROM movie WHERE display_title LIKE :title")
    fun fetchMoviesByTitle(title: String): List<Movie>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movies: List<Movie>)

    @Insert
    suspend fun insertMovie(movie: Movie)

    @Update
    suspend fun updateMovie(movie: Movie)

    @Query("SELECT * FROM movie WHERE favorite = 1")
    fun fetchFavoriteMovies(): DataSource.Factory<Int, Movie>

    @Query("SELECT * FROM movie WHERE display_title = :title")
    fun fetchMovie(title: String): LiveData<Movie>

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("DELETE FROM movie")
    suspend fun deleteAllMovies()
}