package com.pedro.schwarz.desafioyourdev.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.pedro.schwarz.desafioyourdev.R
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.repository.Failure
import com.pedro.schwarz.desafioyourdev.repository.Success
import com.pedro.schwarz.desafioyourdev.ui.extension.setContent
import com.pedro.schwarz.desafioyourdev.ui.extension.showMessage
import com.pedro.schwarz.desafioyourdev.ui.extension.toggleVisibility
import com.pedro.schwarz.desafioyourdev.ui.recyclerview.MoviesAdapter
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.AppViewModel
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.Components
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.FavoriteMovieListViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteMovieListFragment : Fragment() {

    private val controller by lazy { findNavController() }
    private val viewModel by viewModel<FavoriteMovieListViewModel>()
    private val moviesAdapter by inject<MoviesAdapter>()
    private val appViewModel by sharedViewModel<AppViewModel>()

    private lateinit var movieList: RecyclerView
    private lateinit var emptyList: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchMovies()
        configMovieClick()
        configToggleFavorite()
    }

    private fun configMovieClick() {
        moviesAdapter.onItemClick = { title ->
            goToMovieDetails(title)
        }
    }

    private fun goToMovieDetails(title: String) {
        val action =
            FavoriteMovieListFragmentDirections.actionFavoriteMovieListFragmentToMovieDetailsFragment(
                title
            )
        controller.navigate(action)
    }

    private fun configToggleFavorite() {
        moviesAdapter.onToggleFavorite = { movie ->
            toggleFavorite(movie)
        }
    }

    private fun toggleFavorite(movie: Movie) {
        viewModel.toggleMovieFavorite(movie).observe(this, { result ->
            when (result) {
                is Success -> {
                    showMessage(getString(R.string.review_updated_message))
                }
                is Failure -> {
                    result.error?.let { showMessage(it) }
                }
            }
        })
    }

    private fun fetchMovies() {
        viewModel.fetchFavoriteMovies().observe(this, { result ->
            when (result) {
                is Success -> {
                    result.data?.let { viewModel.setIsEmpty = it.isEmpty() }
                    moviesAdapter.submitList(result.data)
                }
                is Failure -> {
                    result.error?.let { showMessage(it) }
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appViewModel.setComponents = Components(appBar = true, bottomBar = true)
        configMovieList(view)
        setIsEmptyListener(view)
    }

    private fun setIsEmptyListener(view: View) {
        emptyList = view.findViewById(R.id.favorite_movie_list_empty)
        viewModel.isEmpty.observe(viewLifecycleOwner, { isEmpty ->
            emptyList.toggleVisibility(visible = isEmpty)
        })
    }

    private fun configMovieList(view: View) {
        movieList = view.findViewById<RecyclerView>(R.id.favorite_movie_list).apply {
            setContent(false, StaggeredGridLayoutManager.VERTICAL, false, moviesAdapter)
        }
    }
}