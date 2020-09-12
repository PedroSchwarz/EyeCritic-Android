package com.pedro.schwarz.desafioyourdev.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.pedro.schwarz.desafioyourdev.R
import com.pedro.schwarz.desafioyourdev.databinding.FragmentMovieListBinding
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.repository.Failure
import com.pedro.schwarz.desafioyourdev.repository.Resource
import com.pedro.schwarz.desafioyourdev.repository.Success
import com.pedro.schwarz.desafioyourdev.ui.dialog.showDeleteAllMoviesDialog
import com.pedro.schwarz.desafioyourdev.ui.dialog.showDeleteMovieDialog
import com.pedro.schwarz.desafioyourdev.ui.extension.setContent
import com.pedro.schwarz.desafioyourdev.ui.extension.showMessage
import com.pedro.schwarz.desafioyourdev.ui.recyclerview.MoviesAdapter
import com.pedro.schwarz.desafioyourdev.ui.recyclerview.callback.SwipeCallback
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.AppViewModel
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.Components
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.MovieListViewModel
import jp.wasabeef.recyclerview.animators.FlipInBottomXAnimator
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MovieListFragment : Fragment() {

    private val controller by lazy { findNavController() }
    private val viewModel by viewModel<MovieListViewModel>()
    private val appViewModel by sharedViewModel<AppViewModel>()
    private val moviesAdapter by inject<MoviesAdapter> { parametersOf(true) }

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
                    setEmptyList(result)
                    moviesAdapter.submitList(result.data)
                }
                is Failure -> {
                    result.error?.let { showMessage(it) }
                }
            }
            viewModel.setIsLoading = false
        })
    }

    private fun setEmptyList(result: Resource<List<Movie>>) {
        result.data?.let { viewModel.setIsEmpty = it.isEmpty() }
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
        val binding = FragmentMovieListBinding.inflate(inflater, container, false)
        setBinding(binding)
        return binding.root
    }

    private fun setBinding(binding: FragmentMovieListBinding) {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        handleOnRefresh(binding)
        handleOnFetchLastestReviews(binding)
        configList(binding)
    }

    private fun configList(binding: FragmentMovieListBinding) {
        binding.movieList.apply {
            setContent(false, StaggeredGridLayoutManager.VERTICAL, false, moviesAdapter)
            itemAnimator = FlipInBottomXAnimator().apply { addDuration = 300 }
            configSwipe()
        }
    }

    private fun RecyclerView.configSwipe() {
        val touchHelper =
            ItemTouchHelper(SwipeCallback(this@MovieListFragment::deleteMovie))
        touchHelper.attachToRecyclerView(this)
    }

    private fun handleOnFetchLastestReviews(binding: FragmentMovieListBinding) {
        binding.onFetchLatestReviews = View.OnClickListener { refreshMovies() }
    }

    private fun handleOnRefresh(binding: FragmentMovieListBinding) {
        binding.onListRefresh = SwipeRefreshLayout.OnRefreshListener { refreshMovies() }
    }

    private fun deleteMovie(position: Int) {
        val movie = moviesAdapter.currentList[position]
        showDeleteMovieDialog(
            requireContext(),
            movie.display_title,
            onCancel = { reloadData(position) },
            onConfirm = {
                viewModel.deleteMovie(movie).observe(viewLifecycleOwner, { result ->
                    when (result) {
                        is Success -> {
                            showMessage(getString(R.string.review_deleted_message))
                        }
                        is Failure -> {
                            result.error?.let { showMessage(it) }
                        }
                    }
                })
            },
        )
    }

    private fun reloadData(position: Int) {
        moviesAdapter.notifyItemRemoved(position)
        moviesAdapter.notifyItemInserted(position)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appViewModel.setComponents = Components(appBar = true, bottomBar = true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.movie_list_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.movie_list_delete_all -> {
                showDeleteAllMoviesDialog(requireContext(), onConfirm = {
                    deleteAllMovies()
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteAllMovies() {
        viewModel.deleteAllMovies().observe(viewLifecycleOwner, { result ->
            when (result) {
                is Success -> {
                    showMessage(getString(R.string.all_reviews_deleted_message))
                }
                is Failure -> {
                    result.error?.let { showMessage(it) }
                }
            }
        })
    }
}