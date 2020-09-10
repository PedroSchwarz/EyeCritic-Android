package com.pedro.schwarz.desafioyourdev.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.pedro.schwarz.desafioyourdev.R
import com.pedro.schwarz.desafioyourdev.retrofit.client.MovieClient
import com.pedro.schwarz.desafioyourdev.ui.extension.setContent
import com.pedro.schwarz.desafioyourdev.ui.extension.showMessage
import com.pedro.schwarz.desafioyourdev.ui.recyclerview.MoviesAdapter
import org.koin.android.ext.android.inject

class MovieListFragment : Fragment() {

    private val movieClient by inject<MovieClient>()
    private val moviesAdapter by inject<MoviesAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieClient.fetchMovies(
            onSuccess = {
                it.results.forEach { el ->
                    Log.i("MOVIE", el.display_title)
                }
                moviesAdapter.submitList(it.results)
            },
            onFailure = { error -> showMessage(error) },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.movie_list).apply {
            setContent(false, StaggeredGridLayoutManager.VERTICAL, false, moviesAdapter)
        }
    }
}