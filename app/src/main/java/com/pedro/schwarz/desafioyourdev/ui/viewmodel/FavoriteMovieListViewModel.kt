package com.pedro.schwarz.desafioyourdev.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.repository.MovieRepository
import com.pedro.schwarz.desafioyourdev.repository.Resource
import kotlinx.coroutines.Job

class FavoriteMovieListViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    private val job = Job()

    private val _isEmpty = MutableLiveData<Boolean>().also { it.value = false }
    val isEmpty: LiveData<Boolean>
        get() = _isEmpty
    var setIsEmpty: Boolean = false
        set(value) {
            field = value
            _isEmpty.value = value
        }

    fun fetchFavoriteMovies(): LiveData<Resource<PagedList<Movie>>> =
        movieRepository.fetchFavoriteMovies()

    fun toggleMovieFavorite(movie: Movie) = movieRepository.toggleMovieFavorite(movie, job = job)

    fun deleteMovie(movie: Movie) = movieRepository.deleteMovie(movie, job = job)

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}