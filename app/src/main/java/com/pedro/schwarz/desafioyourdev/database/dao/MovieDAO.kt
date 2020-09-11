package com.pedro.schwarz.desafioyourdev.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pedro.schwarz.desafioyourdev.model.Movie

@Dao
interface MovieDAO {

    @Query("SELECT * FROM movie")
    fun fetchMovies(): LiveData<List<Movie>>

    @Query("SELECT * FROM movie WHERE display_title LIKE :title")
    fun fetchMoviesByTitle(title: String): LiveData<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertMovie(movies: List<Movie>)

    @Insert
    fun insertMovie(movie: Movie)

    @Update
    fun updateMovie(movie: Movie)

    @Query("SELECT * FROM movie WHERE favorite = 1")
    fun fetchFavoriteMovies(): LiveData<List<Movie>>

    @Query("SELECT * FROM movie WHERE display_title = :title")
    fun fetchMovie(title: String): LiveData<Movie>
}