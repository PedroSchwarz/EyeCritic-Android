package com.pedro.schwarz.desafioyourdev.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pedro.schwarz.desafioyourdev.R
import com.pedro.schwarz.desafioyourdev.retrofit.client.MovieClient
import com.pedro.schwarz.desafioyourdev.ui.extension.showMessage
import org.koin.android.ext.android.inject

class MovieListFragment : Fragment() {

    private val movieClient by inject<MovieClient>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieClient.fetchMovies(onSuccess = {
            Log.i("MOVIES", it.num_results.toString())
        }, onFailure = { error -> showMessage(error) })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }
}