package com.pedro.schwarz.desafioyourdev.ui.fragment

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.MovieListViewModel
import jp.wasabeef.recyclerview.animators.OvershootInLeftAnimator
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieListFragment : Fragment(), SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private val controller by lazy { findNavController() }
    private val viewModel by viewModel<MovieListViewModel>()
    private val moviesAdapter by inject<MoviesAdapter>()
    private val appViewModel by sharedViewModel<AppViewModel>()

    private lateinit var movieListRefresh: SwipeRefreshLayout
    private lateinit var movieList: RecyclerView
    private lateinit var loadingSpinner: ProgressBar
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
            MovieListFragmentDirections.actionMovieListFragmentToMovieDetailsFragment(title)
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
        viewModel.setIsLoading = true
        viewModel.fetchMovies().observe(this, { result ->
            when (result) {
                is Success -> {
                    result.data?.let { viewModel.setIsEmpty = it.isEmpty() }
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
                    showMessage(getString(R.string.list_updated_message))
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
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_movie_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appViewModel.setComponents = Components(appBar = true, bottomBar = true)
        configListRefresh(view)
        configMovieList(view)
        setIsLoadingListener(view)
        setIsEmptyListener(view)
        setIsRefreshingListener()
    }

    private fun setIsEmptyListener(view: View) {
        emptyList = view.findViewById(R.id.movie_list_empty_message)
        viewModel.isEmpty.observe(viewLifecycleOwner, { isEmpty ->
            emptyList.toggleVisibility(visible = isEmpty && !viewModel.setIsLoading)
        })
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
            itemAnimator = OvershootInLeftAnimator().apply { addDuration = 300 }
        }
    }

    private fun configListRefresh(view: View) {
        movieListRefresh = view.findViewById<SwipeRefreshLayout>(R.id.movie_list_refresh).apply {
            setOnRefreshListener { refreshMovies() }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.movie_list_menu, menu)
        val search = menu.findItem(R.id.movie_list_search)
        val searchView = search.actionView as SearchView
        searchView.apply {
            isSubmitButtonEnabled = true
            setOnQueryTextListener(this@MovieListFragment)
            inputType = InputType.TYPE_CLASS_TEXT.or(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
            setOnCloseListener(this@MovieListFragment)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            if (it.trim().isNotEmpty()) {
                fetchMoviesByTitle(query)
            }
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    override fun onClose(): Boolean {
        fetchMovies()
        return false
    }

    private fun fetchMoviesByTitle(title: String) {
        viewModel.fetchMoviesByTitle(title)
    }
}