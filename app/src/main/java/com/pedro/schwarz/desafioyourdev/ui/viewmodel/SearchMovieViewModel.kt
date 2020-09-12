package com.pedro.schwarz.desafioyourdev.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pedro.schwarz.desafioyourdev.repository.MovieRepository

class SearchMovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val _isEmpty = MutableLiveData<Boolean>().also { it.value = false }
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty
    var setIsEmpty: Boolean = false
        set(value) {
            field = value
            _isEmpty.value = value
        }

    fun fetchMoviesByTitle(title: String) = movieRepository.fetchMoviesByTitle(title)
}