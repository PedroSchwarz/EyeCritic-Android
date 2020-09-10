package com.pedro.schwarz.desafioyourdev.di.module

import com.pedro.schwarz.desafioyourdev.repository.MovieRepository
import com.pedro.schwarz.desafioyourdev.retrofit.client.MovieClient
import com.pedro.schwarz.desafioyourdev.retrofit.service.MovieService
import com.pedro.schwarz.desafioyourdev.ui.recyclerview.MoviesAdapter
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.MovieListViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val retrofitModule = module {
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://api.nytimes.com/svc/movies/v2/reviews/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single<MovieService> { get<Retrofit>().create(MovieService::class.java) }
    single<MovieClient> { MovieClient(get()) }
}

val daoModule = module {
    single<MovieRepository> { MovieRepository(get()) }
}

val uiModule = module {
    factory<MoviesAdapter> { MoviesAdapter() }
}

val viewModelModule = module {
    viewModel<MovieListViewModel> { MovieListViewModel(get()) }
}