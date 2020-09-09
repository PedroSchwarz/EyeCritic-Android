package com.pedro.schwarz.desafioyourdev.retrofit

import com.pedro.schwarz.desafioyourdev.retrofit.service.MovieService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppRetrofit {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.nytimes.com/svc/movies/v2/reviews/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val movieService by lazy { retrofit.create(MovieService::class.java) }
}