package com.pedro.schwarz.desafioyourdev.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pedro.schwarz.desafioyourdev.database.dao.MovieDAO
import com.pedro.schwarz.desafioyourdev.model.Movie

@Database(entities = [Movie::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getMovieDAO(): MovieDAO
}