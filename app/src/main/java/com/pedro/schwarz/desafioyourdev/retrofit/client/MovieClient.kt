package com.pedro.schwarz.desafioyourdev.retrofit.client

import com.pedro.schwarz.desafioyourdev.model.MovieResponse
import com.pedro.schwarz.desafioyourdev.retrofit.AppRetrofit
import com.pedro.schwarz.desafioyourdev.retrofit.service.MovieService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieClient(private val movieService: MovieService = AppRetrofit().movieService) {

    private fun <T> execute(
        call: Call<T>,
        onSuccess: (result: T) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) }
                } else {
                    onFailure("Something went wrong")
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                t.message?.let { onFailure(it) }
            }
        })
    }

    fun fetchMovies(
        onSuccess: (result: MovieResponse) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        execute(
            movieService.fetchMovies(),
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }
}