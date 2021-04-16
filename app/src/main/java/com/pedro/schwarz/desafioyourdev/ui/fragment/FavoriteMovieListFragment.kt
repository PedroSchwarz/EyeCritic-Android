package com.pedro.schwarz.desafioyourdev.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.transition.MaterialElevationScale
import com.pedro.schwarz.desafioyourdev.R
import com.pedro.schwarz.desafioyourdev.databinding.FragmentFavoriteMovieListBinding
import com.pedro.schwarz.desafioyourdev.model.Movie
import com.pedro.schwarz.desafioyourdev.repository.Failure
import com.pedro.schwarz.desafioyourdev.repository.Resource
import com.pedro.schwarz.desafioyourdev.repository.Success
import com.pedro.schwarz.desafioyourdev.ui.dialog.showDeleteMovieDialog
import com.pedro.schwarz.desafioyourdev.ui.extension.setContent
import com.pedro.schwarz.desafioyourdev.ui.extension.showMessage
import com.pedro.schwarz.desafioyourdev.ui.recyclerview.MoviesAdapter
import com.pedro.schwarz.desafioyourdev.ui.recyclerview.callback.SwipeCallback
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.AppViewModel
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.Components
import com.pedro.schwarz.desafioyourdev.ui.viewmodel.FavoriteMovieListViewModel
import jp.wasabeef.recyclerview.animators.FlipInBottomXAnimator
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoriteMovieListFragment : Fragment() {

    private val controller by lazy { findNavController() }
    private val viewModel by viewModel<FavoriteMovieListViewModel>()
    private val appViewModel by sharedViewModel<AppViewModel>()
    private val moviesAdapter by inject<MoviesAdapter> { parametersOf(true) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchMovies()
        configMovieClick()
        configToggleFavorite()
    }

    private fun configMovieClick() {
        moviesAdapter.onItemClick = { title, itemView ->
            goToMovieDetails(title, itemView)
        }
    }

    private fun goToMovieDetails(title: String, itemView: View) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = 300
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = 300
        }
        val extras = FragmentNavigatorExtras(itemView to title)
        val action =
            FavoriteMovieListFragmentDirections.actionFavoriteMovieListFragmentToMovieDetailsFragment(
                title
            )
        controller.navigate(action, extras)
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
        viewModel.fetchFavoriteMovies().observe(this, { result: Resource<PagedList<Movie>> ->
            when (result) {
                is Success -> {
                    result.data?.let {
                        viewModel.setIsEmpty = result.data.isNullOrEmpty()
                    }
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
        val binding = FragmentFavoriteMovieListBinding.inflate(inflater, container, false)
        setBinding(binding)
        return binding.root
    }

    private fun setBinding(binding: FragmentFavoriteMovieListBinding) {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        configList(binding)
    }

    private fun configList(binding: FragmentFavoriteMovieListBinding) {
        binding.favoriteMovieList.apply {
            setContent(false, StaggeredGridLayoutManager.VERTICAL, false, moviesAdapter)
            itemAnimator = FlipInBottomXAnimator().apply { addDuration = 300 }
            configSwipe()
        }
    }

    private fun RecyclerView.configSwipe() {
        val touchHelper =
            ItemTouchHelper(SwipeCallback(this@FavoriteMovieListFragment::deleteMovie))
        touchHelper.attachToRecyclerView(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appViewModel.setComponents = Components(appBar = true, bottomBar = true)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    private fun deleteMovie(position: Int) {
        moviesAdapter.currentList?.let {
            it[position]?.let { movie ->
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
                                    result.error?.let { error -> showMessage(error) }
                                }
                            }
                        })
                    },
                )
            }
        }
    }

    private fun reloadData(position: Int) {
        moviesAdapter.notifyItemRemoved(position)
        moviesAdapter.notifyItemInserted(position)
    }
}