package com.pedro.schwarz.desafioyourdev.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.repository.MovieRepository

class FavoriteMovieListViewModel(private val movieRepository: MovieRepository) : ViewModel() {

    fun fetchFavoriteMovies() = movieRepository.fetchFavoriteMovies()

    fun toggleMovieFavorite(movie: Movie) = movieRepository.toggleMovieFavorite(movie)
}