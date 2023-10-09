package com.pedro.schwarz.desafioyourdev.retrofit.service

import com.pedro.schwarz.desafioyourdev.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "pFhpA36Zm3zAqJIZw0ZsxH9MOtWKT5yH"

interface MovieService {

    @GET("articlesearch.json?q=movie&api-key=$API_KEY")
    suspend fun fetchMovies(): MovieResponse

    @GET("search.json?api-key=$API_KEY")
    suspend fun fetchMoviesByTitle(@Query("query") title: String): MovieResponse
}
