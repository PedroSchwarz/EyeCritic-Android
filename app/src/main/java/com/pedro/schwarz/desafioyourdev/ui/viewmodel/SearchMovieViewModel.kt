package com.pedro.schwarz.desafioyourdev.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.repository.MovieRepository
import com.pedro.schwarz.desafioyourdev.repository.Resource
import kotlinx.coroutines.Job

class SearchMovieViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val job = Job()

    private val _isEmpty = MutableLiveData<Boolean>().also { it.value = false }
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty
    var setIsEmpty: Boolean = false
        set(value) {
            field = value
            _isEmpty.value = value
        }

    fun fetchMoviesByTitle(title: String): LiveData<Resource<PagedList<Movie>>> =
        movieRepository.fetchMoviesByTitle(title, job = job)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}