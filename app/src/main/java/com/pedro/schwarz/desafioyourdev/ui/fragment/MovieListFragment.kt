package com.pedro.schwarz.desafioyourdev.ui.fragment

import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pedro.schwarz.desafioyourdev.R
import com.pedro.schwarz.desafioyourdev.databinding.FragmentMovieListBinding
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.repository.Failure
import com.pedro.schwarz.desafioyourdev.repository.Success
import com.pedro.schwarz.desafioyourdev.ui.extension.setContent
import com.pedro.schwarz.desafioyourdev.ui.extension.showMessage
import com.pedro.schwarz.desafioyourdev.ui.recyclerview.MoviesAdapter
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.AppViewModel
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.Components
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.MovieListViewModel
import jp.wasabeef.recyclerview.animators.FlipInBottomXAnimator
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MovieListFragment : Fragment(), SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private val controller by lazy { findNavController() }
    private val viewModel by viewModel<MovieListViewModel>()
    private val moviesAdapter by inject<MoviesAdapter>()
    private val appViewModel by sharedViewModel<AppViewModel>()

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

    private fun fetchMoviesByTitle(title: String) {
        viewModel.fetchMoviesByTitle(title)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding = FragmentMovieListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.onListRefresh = SwipeRefreshLayout.OnRefreshListener { refreshMovies() }
        binding.movieList.apply {
            setContent(false, StaggeredGridLayoutManager.VERTICAL, false, moviesAdapter)
            itemAnimator = FlipInBottomXAnimator().apply { addDuration = 300 }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appViewModel.setComponents = Components(appBar = true, bottomBar = true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.movie_list_menu, menu)
        val search = menu.findItem(R.id.movie_list_search)
        val searchView = search.actionView as SearchView
        searchView.apply {
            isSubmitButtonEnabled = true
            inputType = InputType.TYPE_CLASS_TEXT.or(InputType.TYPE_TEXT_FLAG_CAP_WORDS)
            setOnQueryTextListener(this@MovieListFragment)
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
}