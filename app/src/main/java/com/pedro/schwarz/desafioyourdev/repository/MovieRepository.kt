package com.pedro.schwarz.desafioyourdev.repository

import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.pedro.schwarz.desafioyourdev.UiThreadExecutor
import com.pedro.schwarz.desafioyourdev.database.dao.MovieDAO
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.model.MovieResult
import com.pedro.schwarz.desafioyourdev.retrofit.service.MovieService
import kotlinx.coroutines.*
import java.io.IOException

private const val PAGED_LIST_SIZE: Int = 8

class MovieRepository(private val movieDAO: MovieDAO, private val movieService: MovieService) {

    // Cacheable data fetched from database or API
    private val _movies =
        MediatorLiveData<Resource<PagedList<Movie>>>().also { Success<PagedList<Movie>>(data = null) }

    // Cacheable data fetched from database or API
    private val _favoriteMovies =
        MediatorLiveData<Resource<PagedList<Movie>>>().also { Success<PagedList<Movie>>(data = null) }

    // Cacheable data fetched from database or API
    private val _searchedMovies =
        MutableLiveData<Resource<PagedList<Movie>>>().also { Success<PagedList<Movie>>(data = null) }

    // Fetching from database
    fun fetchMovies(): LiveData<Resource<PagedList<Movie>>> {
        // Listen to database changes
        _movies.addSource(movieDAO.fetchMovies().toLiveData(pageSize = PAGED_LIST_SIZE)) { result ->
            // If is empty
            if (result.isEmpty()) {
                // Return empty list
                _movies.value = Success(data = null)
            } else {
                // Else return results
                _movies.value = Success(data = result)
            }
        }
        // Return mediator
        return _movies
    }

    // Fetch movies by title from database
    fun fetchMoviesByTitle(title: String, job: Job = Job()): LiveData<Resource<PagedList<Movie>>> {
        CoroutineScope(Dispatchers.IO + job).launch {
            try {
                // Check movies with title
                val movies = movieDAO.fetchMoviesByTitle("%$title%")
                val pagedListMovies =
                    PagedList.Builder(ListDataSource<Movie>(movies), PAGED_LIST_SIZE)
                        .setNotifyExecutor(UiThreadExecutor())
                        .setFetchExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                        .build()
                // If result it's empty
                if (movies.isEmpty()) {
                    // Fetch from API
                    fetchMoviesByTitleAPI(title, job = job)
                } else {
                    // Else set live data data with result
                    _searchedMovies.postValue(Success(data = pagedListMovies))
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
    fun fetchMoviesAPI(job: Job = Job()): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        // Call movie client
        CoroutineScope(Dispatchers.IO + job).launch {
            try {
                val response = movieService.fetchMovies()
                val movies = convertToMovies(result = response.results)
                movieDAO.insertMovie(movies)
                liveData.postValue(Success())
            } catch (e: IOException) {
                _movies.postValue(Failure(error = e.message))
                liveData.postValue(Failure())
            }
        }
        // Return live data
        return liveData
    }

    // Fetching movies by title from API
    private fun fetchMoviesByTitleAPI(title: String, job: Job) {
        // Call movie client
        CoroutineScope(Dispatchers.IO + job).launch {
            try {
                val response = movieService.fetchMoviesByTitle(title)
                val movies = convertToMovies(result = response.results)
                movieDAO.insertMovie(movies)
                val pagedListMovies =
                    PagedList.Builder(ListDataSource<Movie>(movies), PAGED_LIST_SIZE)
                        .setNotifyExecutor(UiThreadExecutor())
                        .setFetchExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                        .build()
                _searchedMovies.postValue(Success(data = pagedListMovies))
            } catch (e: IOException) {
                _searchedMovies.postValue(Failure(error = e.message))
            }
        }
    }

    // Update movie favorite status in database
    fun toggleMovieFavorite(movie: Movie, job: Job = Job()): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        CoroutineScope(Dispatchers.IO + job).launch {
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
    fun fetchFavoriteMovies(): LiveData<Resource<PagedList<Movie>>> {
        // Listen to database favorites changes
        _favoriteMovies.addSource(
            movieDAO.fetchFavoriteMovies().toLiveData(pageSize = PAGED_LIST_SIZE)
        ) { result ->
            // Set favorite movies mediator with Result
            _favoriteMovies.value = Success(data = result)
        }
        // Return mediator
        return _favoriteMovies
    }

    // Fetch movie from database
    fun fetchMovie(title: String, job: Job = Job()): LiveData<Resource<Movie>> {
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
                }, job = job)
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
        onFailure: (error: String) -> Unit,
        job: Job
    ) {
        // Call movie client
        CoroutineScope(Dispatchers.IO + job).launch {
            try {
                val response = movieService.fetchMoviesByTitle(title)
                val movies = convertToMovies(result = response.results)
                if (movies.isEmpty()) {
                    onFailure("Movie not found")
                } else {
                    onSuccess(movies[0])
                }
            } catch (e: IOException) {
                onFailure("Movie not found")
            }
        }
    }

    // Delete movie from database
    fun deleteMovie(movie: Movie, job: Job = Job()): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        CoroutineScope(Dispatchers.IO + job).launch {
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
    fun deleteAllMovies(job: Job = Job()): LiveData<Resource<Unit>> {
        val liveData = MutableLiveData<Resource<Unit>>()
        CoroutineScope(Dispatchers.IO + job).launch {
            try {
                movieDAO.deleteAllMovies()
                liveData.postValue(Success())
            } catch (exception: IOException) {
                liveData.postValue(Failure(error = exception.message))
            }
        }
        return liveData
    }

    // Convert API response data to usable database Movie Entity
    private fun convertToMovies(result: List<MovieResult>): List<Movie> {
        val movies = mutableListOf<Movie>()
        for (movieResult in result) {
            movies.add(movieResult.toMovie())
        }
        return movies
    }
}