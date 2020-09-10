package com.pedro.schwarz.desafioyourdev.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pedro.schwarz.desafioyourdev.R
import com.pedro.schwarz.desafioyourdev.repository.Failure
import com.pedro.schwarz.desafioyourdev.repository.Success
import com.pedro.schwarz.desafioyourdev.ui.extension.setContent
import com.pedro.schwarz.desafioyourdev.ui.extension.showMessage
import com.pedro.schwarz.desafioyourdev.ui.extension.toggleVisibility
import com.pedro.schwarz.desafioyourdev.ui.recyclerview.MoviesAdapter
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.MovieListViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieListFragment : Fragment() {

    private val viewModel by viewModel<MovieListViewModel>()
    private val moviesAdapter by inject<MoviesAdapter>()

    private lateinit var movieListRefresh: SwipeRefreshLayout
    private lateinit var movieList: RecyclerView
    private lateinit var loadingSpinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchMovies()
    }

    private fun fetchMovies() {
        viewModel.setIsLoading = true
        viewModel.fetchMovies().observe(this, { result ->
            when (result) {
                is Success -> {
                    moviesAdapter.submitList(result.data)
                }
                is Failure -> {
                    result.error?.let { showMessage(it) }
                }
            }
            viewModel.setIsLoading = false
        })
    }

    private fun refreshMovies() {
        viewModel.setIsRefreshing = true
        viewModel.refreshMovies().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Success -> {
                    viewModel.setIsRefreshing = false
                }
                is Failure -> {
                    viewModel.setIsRefreshing = false
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configListRefresh(view)
        configMovieList(view)
        setIsLoadingListener(view)
        setIsRefreshingListener()
    }

    private fun setIsRefreshingListener() {
        viewModel.isRefreshing.observe(viewLifecycleOwner, { isRefreshing ->
            movieListRefresh.isRefreshing = isRefreshing
        })
    }

    private fun setIsLoadingListener(view: View) {
        loadingSpinner = view.findViewById(R.id.movie_list_loading)
        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            movieList.toggleVisibility(visible = !isLoading)
            loadingSpinner.toggleVisibility(visible = isLoading)
        })
    }

    private fun configMovieList(view: View) {
        movieList = view.findViewById<RecyclerView>(R.id.movie_list).apply {
            setContent(false, StaggeredGridLayoutManager.VERTICAL, false, moviesAdapter)
        }
    }

    private fun configListRefresh(view: View) {
        movieListRefresh = view.findViewById<SwipeRefreshLayout>(R.id.movie_list_refresh).apply {
            setOnRefreshListener { refreshMovies() }
        }
    }
}