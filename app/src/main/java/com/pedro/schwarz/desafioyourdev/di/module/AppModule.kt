package com.pedro.schwarz.desafioyourdev.di.module

import com.pedro.schwarz.desafioyourdev.retrofit.client.MovieClient
import org.koin.dsl.module

val retrofitModule = module {
    single<MovieClient> { MovieClient() }
}