package com.pedro.schwarz.desafioyourdev.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.pedro.schwarz.desafioyourdev.database.dao.MovieDAO
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.retrofit.client.MovieClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class MovieRepository(private val movieDAO: MovieDAO, private val movieClient: MovieClient) {

    private val _movies =
        MediatorLiveData<Resource<List<Movie>>>().also { Success<List<Movie>>(data = arrayListOf()) }

    fun fetchMovies(): LiveData<Resource<List<Movie>>> {
        _movies.addSource(movieDAO.fetchMovies()) { result ->
            if (result.isEmpty()) {
                fetchMoviesAPI()
            } else {
                _movies.value = Success(data = result)
            }
        }
        return _movies
    }

    fun fetchMoviesByTitle(title: String): LiveData<Resource<List<Movie>>> {
        _movies.addSource(movieDAO.fetchMoviesByTitle("%$title%")) { result ->
            if (result.isEmpty()) {
                fetchMoviesByTitleAPI(title)
            } else {
                _movies.value = Success(data = result)
            }
        }
        return _movies
    }

    fun fetchMoviesAPI(): LiveData<Resource<Void>> {
        val liveData = MutableLiveData<Resource<Void>>()
        movieClient.fetchMovies(
            onSuccess = { result ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        movieDAO.insertMovie(result)
                    } catch (exception: IOException) {
                        _movies.value = Failure(error = exception.message)
                    }
                }
                liveData.value = Success(data = null)
            },
            onFailure = { error ->
                _movies.value = Failure(error = error)
                liveData.value = Failure(error = null)
            },
        )
        return liveData
    }

    private fun fetchMoviesByTitleAPI(title: String) {
        movieClient.fetchMoviesByTitle(
            title = title,
            onSuccess = { result ->
                _movies.value = Success(data = result)
            },
            onFailure = {
                _movies.value = Success(data = arrayListOf())
            },
        )
    }
}