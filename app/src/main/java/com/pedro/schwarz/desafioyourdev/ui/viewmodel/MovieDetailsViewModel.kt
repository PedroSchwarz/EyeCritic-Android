package com.pedro.schwarz.desafioyourdev.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.repository.MovieRepository

class MovieDetailsViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val _isMenuOpen = MutableLiveData<Boolean>().also { it.value = false }
    val isMenuOpen: LiveData<Boolean>
        get() = _isMenuOpen
    var setIsMenuOpen: Boolean = false
        set(value) {
            field = value
            _isMenuOpen.value = value
        }

    fun fetchMovie(title: String) = movieRepository.fetchMovie(title)

    fun toggleMovieFavorite(movie: Movie) = movieRepository.toggleMovieFavorite(movie)
}