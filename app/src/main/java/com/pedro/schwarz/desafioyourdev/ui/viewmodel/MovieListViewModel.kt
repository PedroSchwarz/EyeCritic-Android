package com.pedro.schwarz.desafioyourdev.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.repository.MovieRepository

class MovieListViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>().also { it.value = true }
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    var setIsLoading: Boolean = true
        set(value) {
            field = value
            _isLoading.value = value
        }

    private val _isEmpty = MutableLiveData<Boolean>().also { it.value = false }
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty
    var setIsEmpty: Boolean = false
        set(value) {
            field = value
            _isEmpty.value = value
        }


    private val _isRefreshing = MutableLiveData<Boolean>().also { it.value = false }
    val isRefreshing: LiveData<Boolean>
        get() = _isRefreshing
    var setIsRefreshing: Boolean = false
        set(value) {
            field = value
            _isRefreshing.value = value
        }

    fun fetchMovies() = movieRepository.fetchMovies()

    fun refreshMovies() = movieRepository.fetchMoviesAPI()

    fun fetchMoviesByTitle(title: String) = movieRepository.fetchMoviesByTitle(title)

    fun toggleMovieFavorite(movie: Movie) = movieRepository.toggleMovieFavorite(movie)

    fun deleteMovie(movie: Movie) = movieRepository.deleteMovie(movie)
}
