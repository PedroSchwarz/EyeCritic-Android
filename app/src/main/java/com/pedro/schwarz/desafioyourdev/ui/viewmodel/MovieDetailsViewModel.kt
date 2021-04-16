package com.pedro.schwarz.desafioyourdev.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.repository.MovieRepository
import com.pedro.schwarz.desafioyourdev.repository.Resource
import kotlinx.coroutines.Job

class MovieDetailsViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val job = Job()

    private val _isLoading = MutableLiveData<Boolean>().also { it.value = true }
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    var setIsLoading: Boolean = true
        set(value) {
            field = value
            _isLoading.value = value
        }

    private val _isMenuOpen = MutableLiveData<Boolean>().also { it.value = false }
    val isMenuOpen: LiveData<Boolean>
        get() = _isMenuOpen
    var setIsMenuOpen: Boolean = false
        set(value) {
            field = value
            _isMenuOpen.value = value
        }

    fun fetchMovie(title: String): LiveData<Resource<Movie>> = movieRepository.fetchMovie(title, job = job)

    fun toggleMovieFavorite(movie: Movie) = movieRepository.toggleMovieFavorite(movie, job = job)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}