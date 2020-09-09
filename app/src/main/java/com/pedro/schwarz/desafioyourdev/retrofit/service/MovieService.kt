package com.pedro.schwarz.desafioyourdev.retrofit.service

import com.pedro.schwarz.desafioyourdev.model.MovieResponse
import retrofit2.Call
import retrofit2.http.GET

private const val API_KEY = "pFhpA36Zm3zAqJIZw0ZsxH9MOtWKT5yH"

interface MovieService {

    @GET("all.json?api-key=$API_KEY")
    fun fetchMovies(): Call<MovieResponse>
}