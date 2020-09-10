package com.pedro.schwarz.desafioyourdev.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.retrofit.client.MovieClient

class MovieRepository(private val movieClient: MovieClient) {

    private val _movies =
        MutableLiveData<Resource<List<Movie>>>().also { Success<List<Movie>>(data = arrayListOf()) }

    fun fetchMovies(): LiveData<Resource<List<Movie>>> {
        fetchMoviesAPI()
        return _movies
    }

    fun fetchMoviesByTitle(title: String): LiveData<Resource<List<Movie>>> {
        fetchMoviesByTitleAPI(title)
        return _movies
    }

    fun fetchMoviesAPI(): LiveData<Resource<Void>> {
        val liveData = MutableLiveData<Resource<Void>>()
        movieClient.fetchMovies(
            onSuccess = { result ->
                _movies.value = Success(data = result.results)
                liveData.value = Success(data = null)
            },
            onFailure = { error ->
                _movies.value = Failure(error = error)
                liveData.value = Failure(error = null)
            },
        )
        return liveData
    }

    fun fetchMoviesByTitleAPI(title: String) {
        movieClient.fetchMoviesByTitle(
            title = title,
            onSuccess = { result ->
                _movies.value = Success(data = result.results)
            },
            onFailure = {
                _movies.value = Success(data = arrayListOf())
            },
        )
    }
}