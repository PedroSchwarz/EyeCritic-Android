package com.pedro.schwarz.desafioyourdev.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pedro.schwarz.desafioyourdev.model.Movie

@Dao
interface MovieDAO {

    @Query("SELECT * FROM movie")
    fun fetchMovies(): LiveData<List<Movie>>

    @Query("SELECT * FROM movie WHERE display_title LIKE :title")
    fun fetchMoviesByTitle(title: String): LiveData<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movies: List<Movie>)
}