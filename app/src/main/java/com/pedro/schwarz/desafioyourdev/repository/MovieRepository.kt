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

    // Cacheable data fetched from database or API
    private val _movies =
        MediatorLiveData<Resource<List<Movie>>>().also { Success<List<Movie>>(data = arrayListOf()) }

    // Cacheable data fetched from database or API
    private val _favoriteMovies =
        MediatorLiveData<Resource<List<Movie>>>().also { Success<List<Movie>>(data = arrayListOf()) }

    // Cacheable data fetched from database or API
    private val _searchedMovies =
        MutableLiveData<Resource<List<Movie>>>().also { Success<List<Movie>>(data = arrayListOf()) }

    // Fetching from database
    fun fetchMovies(): LiveData<Resource<List<Movie>>> {
        // Listen to database changes
        _movies.addSource(movieDAO.fetchMovies()) { result ->
            // If is empty
            if (result.isEmpty()) {
                // Return empty list
                _movies.value = Success(data = arrayListOf())
            } else {
                // Else return results
                _movies.value = Success(data = result)
            }
        }
        // Return mediator
        return _movies
    }

    // Fetch movies by title from database
    fun fetchMoviesByTitle(title: String): LiveData<Resource<List<Movie>>> {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Check movies with title
                val movies = movieDAO.fetchMoviesByTitle("%$title%")
                // If result it's empty
                if (movies.isEmpty()) {
                    // Fetch from API
                    fetchMoviesByTitleAPI(title)
                } else {
                    // Else set live data data with result
                    _searchedMovies.postValue(Success(data = movies))
                }
            } catch (exception: IOException) {
                // Return Failure
                _searchedMovies.value = Failure(error = exception.message)
            }
        }
        // return live data
        return _searchedMovies
    }

    // Fetching movies from API
    fun fetchMoviesAPI(): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        // Call movie client
        movieClient.fetchMovies(
            // On success callback
            onSuccess = { result ->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Save fetched data in database from future searches
                        movieDAO.insertMovie(result)
                    } catch (exception: IOException) {
                        // Return failure
                        _movies.postValue(Failure(error = exception.message))
                    }
                }
                // Return success
                liveData.value = Success()
            },
            // On failure callback
            onFailure = { error ->
                // Set movies with Failure
                _movies.value = Failure(error = error)
                // Return Failure
                liveData.value = Failure()
            },
        )
        // Return live data
        return liveData
    }

    // Fetching movies by title from API
    private fun fetchMoviesByTitleAPI(title: String) {
        // Call movie client
        movieClient.fetchMoviesByTitle(
            title = title,
            // On success callback
            onSuccess = { result ->
                CoroutineScope(Dispatchers.IO).launch {
                    // Save fetched data in database from future searches
                    movieDAO.insertMovie(result)
                }
                // Set searched movies live data with results
                _searchedMovies.value = Success(data = result)
            },
            onFailure = {
                // Set searched movies Failure
                _searchedMovies.value = Failure(error = it)
            },
        )
    }

    // Update movie favorite status in database
    fun toggleMovieFavorite(movie: Movie): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Update movie with new favorite value using data class
                movieDAO.updateMovie(movie.copy(favorite = !movie.favorite))
                // Return Success
                liveData.postValue(Success())
            } catch (exception: IOException) {
                // Return Failure
                liveData.postValue(Failure(error = exception.message))
            }
        }
        // Return live data
        return liveData
    }

    // Fetching favorite movies from database
    fun fetchFavoriteMovies(): LiveData<Resource<List<Movie>>> {
        // Listen to database favorites changes
        _favoriteMovies.addSource(movieDAO.fetchFavoriteMovies()) { result ->
            // Set favorite movies mediator with Result
            _favoriteMovies.value = Success(data = result)
        }
        // Return mediator
        return _favoriteMovies
    }

    // Fetch movie from database
    fun fetchMovie(title: String): LiveData<Resource<Movie>> {
        val liveData = MediatorLiveData<Resource<Movie>>()
        // Listen to database single movie changes
        liveData.addSource(movieDAO.fetchMovie(title)) { result ->
            // Check if movie is in database
            if (result == null) {
                // If it isn't, fetch from API
                fetchMovieAPI(title, onSuccess = {
                    // Return Success
                    liveData.value = Success(data = it)
                }, onFailure = {
                    // Return Failure
                    liveData.value = Failure(error = it)
                })
            } else {
                // If exists, return Success
                liveData.value = Success(data = result)
            }
        }
        // Return live data
        return liveData
    }

    // Fetch single movie from API
    private fun fetchMovieAPI(
        title: String,
        onSuccess: (result: Movie) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        // Call movie client
        movieClient.fetchMoviesByTitle(
            title,
            // On success callback
            onSuccess = {
                // If result is empty
                if (it.isEmpty()) {
                    // Return Failure
                    onFailure("Movie not found")
                } else {
                    // Return first item in results
                    onSuccess(it[0])
                }
            },
            // On failure callback
            onFailure = {
                // Return Failure
                onFailure("Movie not found")
            },
        )
    }

    // Delete movie from database
    fun deleteMovie(movie: Movie): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movieDAO.deleteMovie(movie)
                liveData.postValue(Success())
            } catch (exception: IOException) {
                liveData.postValue(Failure(error = exception.message))
            }
        }
        return liveData
    }

    // Delete all movies from database
    fun deleteAllMovies(): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                movieDAO.deleteAllMovies()
                liveData.postValue(Success())
            } catch (exception: IOException) {
                liveData.postValue(Failure(error = exception.message))
            }
        }
        return liveData
    }
}