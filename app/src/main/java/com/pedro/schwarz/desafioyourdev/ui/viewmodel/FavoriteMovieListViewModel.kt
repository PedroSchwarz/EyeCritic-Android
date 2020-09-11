package com.pedro.schwarz.desafioyourdev.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.repository.MovieRepository

class FavoriteMovieListViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val _isEmpty = MutableLiveData<Boolean>().also { it.value = false }
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty
    var setIsEmpty: Boolean = false
        set(value) {
            field = value
            _isEmpty.value = value
        }

    fun fetchFavoriteMovies() = movieRepository.fetchFavoriteMovies()

    fun toggleMovieFavorite(movie: Movie) = movieRepository.toggleMovieFavorite(movie)
}