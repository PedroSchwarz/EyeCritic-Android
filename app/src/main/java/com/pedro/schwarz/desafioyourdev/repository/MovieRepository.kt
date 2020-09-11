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
    private val _favoriteMovies =
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

    fun fetchMoviesAPI(): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        movieClient.fetchMovies(
            onSuccess = { result ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        movieDAO.insertMovie(result)
                    } catch (exception: IOException) {
                        _movies.value = Failure(error = exception.message)
                    }
                }
                liveData.value = Success()
            },
            onFailure = { error ->
                _movies.value = Failure(error = error)
                liveData.value = Failure()
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

    fun toggleMovieFavorite(movie: Movie): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movieDAO.updateMovie(movie.copy(favorite = !movie.favorite))
                liveData.postValue(Success())
            } catch (exception: IOException) {
                liveData.postValue(Failure(error = exception.message))
            }
        }
        return liveData
    }

    fun fetchFavoriteMovies(): LiveData<Resource<List<Movie>>> {
        _favoriteMovies.addSource(movieDAO.fetchFavoriteMovies()) { result ->
            _favoriteMovies.value = Success(data = result)
        }
        return _favoriteMovies
    }

    fun fetchMovie(title: String): LiveData<Resource<Movie>> {
        val liveData = MediatorLiveData<Resource<Movie>>()
        liveData.addSource(movieDAO.fetchMovie(title)) { result ->
            if (result == null) {
                fetchMovieAPI(title, onSuccess = {
                    liveData.value = Success(data = it)
                }, onFailure = {
                    liveData.value = Failure(error = it)
                })
            } else {
                liveData.value = Success(data = result)
            }
        }
        return liveData
    }

    private fun fetchMovieAPI(
        title: String,
        onSuccess: (result: Movie) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        movieClient.fetchMoviesByTitle(title, onSuccess = {
            if (it.isEmpty()) {
                onFailure("Movie not found")
            } else {
                onSuccess(it[0])
            }
        }, onFailure = {
            onFailure("Movie not found")
        })
    }
}