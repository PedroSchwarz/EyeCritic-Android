package com.pedro.schwarz.desafioyourdev.di.module

import com.pedro.schwarz.desafioyourdev.retrofit.client.MovieClient
import com.pedro.schwarz.desafioyourdev.ui.recyclerview.MoviesAdapter
import org.koin.dsl.module

val retrofitModule = module {
    single<MovieClient> { MovieClient() }
}

val uiModule = module {
    factory<MoviesAdapter> { MoviesAdapter() }
}